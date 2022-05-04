package com.cherit.processing_engine

import com.cherit.models.{Movie, MovieRating, MovieRatingAggregated, MovieRatingAggregatedWithTitle, MovieRatingAnomaly}
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.io.FilePathFilter
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.java.io.TextInputFormat
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.functions.source.FileProcessingMode
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.kafka.clients.consumer.OffsetResetStrategy

import java.util

object ProcessingEngine extends App {
  if (args.length != 5)
    throw NoSuchElementException

  val senv = StreamExecutionEnvironment.getExecutionEnvironment
  val numberOfRetries = 3
  senv.getConfig.setRestartStrategy(RestartStrategies.fixedDelayRestart(numberOfRetries, 0))

//  val filePath = new Path(args(2))
//  val fileFormat = new TextInputFormat(filePath)
//  val localFsURI = "file:///tmp/flink-input"
//  fileFormat.setFilesFilter(FilePathFilter.createDefaultFilter())
//
//  val movieFileStream: DataStream[String] = senv.readFile(fileFormat, localFsURI)
//
//  val movieStream: DataStream[Movie] = movieFileStream.
//    filter(!_.startsWith("ID")).
//    map(_.split(",")).
//    filter(_.length == 3).
//    map(array => Movie(array(0), array(1).toInt, array(2)))

  val source = KafkaSource
    .builder()
    .setBootstrapServers(args(1))
    .setTopics(util.Arrays.asList(args(0)))
    .setGroupId("netflix-ratings-group")
    .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
    .setValueOnlyDeserializer(new SimpleStringSchema())
    .build()

  val inputStream: DataStream[String] = senv.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source")
  val format = new java.text.SimpleDateFormat("yyyy-MM-dd")

  val movieRatingDS: DataStream[MovieRating] = inputStream.
    map(_.split(",")).
    filter(_.length == 4).
    map(array => MovieRating(format.parse(array(0)), array(1), array(2), array(3).toInt)).
    assignTimestampsAndWatermarks(new MovieRatingWatermarkStrategy[MovieRating])

  val aggregatedRatingDS: DataStream[MovieRatingAggregated] = movieRatingDS.
    keyBy(_.movieId).
    window(TumblingEventTimeWindows.of(Time.days(30))).
    aggregate(new MovieRatingAggregator)


  val movieAnomaliesRatingDS: DataStream[MovieRatingAnomaly] = movieRatingDS.
    keyBy(_.movieId).
    window(TumblingEventTimeWindows.of(Time.days(args(2).toInt))).
    aggregate(new MovieRatingAggregator).
    filter(_.ratingCount >= args(3).toInt).
    map(movieRatingAggregated => MovieRatingAnomaly("", "", movieRatingAggregated.movieId, movieRatingAggregated.ratingCount, movieRatingAggregated.ratingSum/movieRatingAggregated.ratingCount)).
    filter(_.ratingsMean >= args(4).toLong).
    process(new MovieRatingAnomalyWindowFunction)

  senv.execute("Movie rating ETL")

}
