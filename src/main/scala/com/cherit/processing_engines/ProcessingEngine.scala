package com.cherit.processing_engines

import com.cherit.aggregators.{MovieRatingAggregator, MovieRatingAnomalyAggregator}
import com.cherit.models._
import com.cherit.process_functions.{MovieRatingAnomalyProcessFunction, MovieRatingProcessFunction}
import com.cherit.rich_functions.{MovieAnomalyRichFunction, MovieResultRichFunction}
import com.cherit.sinks.{JdbcSinkHelper, KafkaSinkHelper}
import com.cherit.watermarks.MovieRatingWatermarkStrategy
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.jdbc.JdbcStatementBuilder
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer

import java.sql.PreparedStatement
import java.util.Properties

object ProcessingEngine {
  def main(args: Array[String]): Unit = {
    if (args.length != 11)
      throw new NoSuchElementException

    val senv = StreamExecutionEnvironment.getExecutionEnvironment
    val numberOfRetries = 3
    senv.getConfig.setRestartStrategy(RestartStrategies.fixedDelayRestart(numberOfRetries, 0))
    senv.registerCachedFile(args(0), "moviesFile")

//    val source = KafkaSourceHelper.get(args(1), args(2), args(3))
//    val inputStream: DataStream[String] = senv.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source")

    val properties = new Properties()
    properties.setProperty("bootstrap.servers", args(1))
    properties.setProperty("group.id", args(3))
    val inputStream = senv.addSource(new FlinkKafkaConsumer[String](args(2), new SimpleStringSchema(), properties))

    val format = new java.text.SimpleDateFormat("yyyy-MM-dd")
    val movieRatingDS: DataStream[MovieRating] = inputStream
      .map(_.split(","))
      .filter(_.length == 4)
      .map(array => MovieRating(format.parse(array(0)), array(1).toInt, array(2), array(3).toInt))
      .assignTimestampsAndWatermarks(new MovieRatingWatermarkStrategy[MovieRating])

    val aggregatedRatingDS: DataStream[MovieRatingResult] = movieRatingDS
      .keyBy(_.movieId)
      .window(TumblingEventTimeWindows.of(Time.days(30)))
      .aggregate(new MovieRatingAggregator, new MovieRatingProcessFunction)

    val aggregatedRatingWithTitleDS: DataStream[MovieRatingResultWithTitle] = aggregatedRatingDS
      .map(new MovieResultRichFunction)
    val mysqlSink = JdbcSinkHelper.get[MovieRatingResultWithTitle](
      "INSERT INTO movie_ratings (window_start, movie_id, title, rating_count, rating_sum, unique_rating_count) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE rating_count=?, rating_sum=?, unique_rating_count=?",
      new JdbcStatementBuilder[MovieRatingResultWithTitle] {
        override def accept(statement: PreparedStatement, movieRating: MovieRatingResultWithTitle): Unit = {
          statement.setLong(1, movieRating.windowStart)
          statement.setInt(2, movieRating.movieId)
          statement.setString(3, movieRating.title)
          statement.setInt(4, movieRating.ratingCount.toInt)
          statement.setInt(5, movieRating.ratingSum.toInt)
          statement.setInt(6, movieRating.uniqueRatingCount.toInt)
          statement.setInt(7, movieRating.ratingCount.toInt)
          statement.setInt(8, movieRating.ratingSum.toInt)
          statement.setInt(9, movieRating.uniqueRatingCount.toInt)
        }
      },
      args(4), args(5), args(6)
    )

    aggregatedRatingWithTitleDS.addSink(mysqlSink)

    val movieAnomaliesRatingDS: DataStream[MovieRatingAnomaly] = movieRatingDS
      .keyBy(_.movieId)
      .window(SlidingEventTimeWindows.of(Time.days(args(7).toInt), Time.days(1)))
      .aggregate(new MovieRatingAnomalyAggregator(), new MovieRatingAnomalyProcessFunction())
      .filter(_.ratingCount >= args(8).toInt)
      .filter(_.ratingMean >= args(9).toLong)

    val movieAnomaliesRatingWithTitleDS: DataStream[MovieRatingAnomalyWithTitle] = movieAnomaliesRatingDS
      .map(new MovieAnomalyRichFunction)

    val kafkaSink = KafkaSinkHelper.get(args(1), args(10))
    movieAnomaliesRatingWithTitleDS.map(_.toString).sinkTo(kafkaSink)
    senv.execute("Netflix Prize Data processing...")
  }
}
