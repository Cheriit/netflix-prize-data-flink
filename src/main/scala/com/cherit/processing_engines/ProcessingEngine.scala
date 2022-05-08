package com.cherit.processing_engines

import com.cherit.aggregators.{MovieRatingAggregator, MovieRatingAnomalyAggregator}
import com.cherit.models._
import com.cherit.process_functions.{MovieRatingAnomalyProcessFunction, MovieRatingProcessFunction}
import com.cherit.sinks.{JdbcSinkHelper, KafkaSinkHelper}
import com.cherit.sources.KafkaSourceHelper
import com.cherit.watermarks.MovieRatingWatermarkStrategy
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.java.io.TextInputFormat
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.triggers.{ContinuousEventTimeTrigger, EventTimeTrigger}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow

import java.sql.Date

object ProcessingEngine {
  def main(args: Array[String]): Unit = {
    if (args.length != 12)
      throw new NoSuchElementException

    val senv = StreamExecutionEnvironment.getExecutionEnvironment
    val numberOfRetries = 3
    senv.getConfig.setRestartStrategy(RestartStrategies.fixedDelayRestart(numberOfRetries, 0))

    val filePath = new Path(args(0))
    val fileFormat = new TextInputFormat(filePath)
    val localFsURI = "file:///tmp/flink-input"
    //  fileFormat.setFilesFilter(FilePathFilter.createDefaultFilter())

    val movieFileStream: DataStream[String] = senv.readFile(fileFormat, localFsURI)

    val movieDS: DataStream[Movie] = movieFileStream
      .filter(!_.startsWith("ID"))
      .map(_.split(","))
      .filter(_.length == 3)
      .map(array => Movie(array(0), array(1).toInt, array(2)))

    val source = KafkaSourceHelper.get(args(1), args(2), args(3))

    val inputStream: DataStream[String] = senv.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source")
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd")

    val movieRatingDS: DataStream[MovieRating] = inputStream
      .map(_.split(","))
      .filter(_.length == 4)
      .map(array => MovieRating(format.parse(array(0)), array(1), array(2), array(3).toInt))
      .assignTimestampsAndWatermarks(new MovieRatingWatermarkStrategy[MovieRating])

    val aggregatedRatingDS: DataStream[MovieRatingResult] = movieRatingDS
      .keyBy(_.movieId)
      .window(TumblingEventTimeWindows.of(Time.days(30)))
      .trigger(ContinuousEventTimeTrigger.of[TimeWindow](if (args(11) == "H") Time.days(30) else Time.seconds(10)))
      .aggregate(new MovieRatingAggregator, new MovieRatingProcessFunction)

    val aggregatedRatingWithTitleDS: DataStream[MovieRatingResultWithTitle] = aggregatedRatingDS
      .join(movieDS)
      .where(_.movieId)
      .equalTo(_.id)
      .window(TumblingEventTimeWindows.of(Time.days(30))) {
        (rating, movie) => MovieRatingResultWithTitle(rating.windowStart, movie.id, movie.title, rating.ratingCount, rating.ratingSum, rating.uniqueRatingCount)
      }

    val mysqlSink = JdbcSinkHelper.get[MovieRatingResultWithTitle](
      "INSERT INTO movie_ratings (window_start, movie_id, title, rating_count, rating_sum, unique_rating_count) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE rating_count=?, rating_sum=?, unique_rating_count=?",
      (statement, movieRating: MovieRatingResultWithTitle) => {
        statement.setDate(1, new Date(movieRating.windowStart))
        statement.setString(2, movieRating.movieId)
        statement.setString(3, movieRating.title)
        statement.setLong(4, movieRating.ratingCount)
        statement.setLong(5, movieRating.ratingSum)
        statement.setLong(6, movieRating.uniqueRatingCount)
        statement.setLong(7, movieRating.ratingCount)
        statement.setLong(8, movieRating.ratingSum)
        statement.setLong(9, movieRating.uniqueRatingCount)
      },
      args(4), "com.mysql.cj.jdbc.Driver", args(5), args(6)
    )
    aggregatedRatingWithTitleDS.addSink(mysqlSink)

    val movieAnomaliesRatingDS: DataStream[MovieRatingAnomaly] = movieRatingDS
      .keyBy(_.movieId)
      .window(SlidingEventTimeWindows.of(Time.days(args(7).toInt), Time.days(1)))
      .aggregate(new MovieRatingAnomalyAggregator(), new MovieRatingAnomalyProcessFunction())
      .filter(_.ratingCount >= args(8).toInt)
      .filter(_.ratingMean >= args(9).toLong)

    val movieAnomaliesRatingWithTitleDS: DataStream[MovieRatingAnomalyWithTitle] = movieAnomaliesRatingDS
      .join(movieDS)
      .where(_.movieId)
      .equalTo(_.id)
      .window(SlidingEventTimeWindows.of(Time.days(args(3).toInt), Time.days(1))) {
        (anomaly, movie) => MovieRatingAnomalyWithTitle(anomaly.windowStart, anomaly.windowStop, movie.title, anomaly.ratingCount, anomaly.ratingMean)
      }

    val kafkaSink = KafkaSinkHelper.get(args(1), args(10))
    movieAnomaliesRatingWithTitleDS.map(_.toString).sinkTo(kafkaSink)
    senv.execute("Netflix Prize Data processing...")
  }
}
