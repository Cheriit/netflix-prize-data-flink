package com.cherit.rich_functions

import com.cherit.models.{MovieRatingAnomaly, MovieRatingAnomalyWithTitle, MovieRatingResult, MovieRatingResultWithTitle}
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration

import scala.io.Source

class MovieAnomalyRichFunction extends RichMapFunction[MovieRatingAnomaly, MovieRatingAnomalyWithTitle]{
  var moviesMap: Map[Int, String] = _

  override def open(parameters: Configuration): Unit = {
    val movieFile = getRuntimeContext.getDistributedCache.getFile("moviesFile")
    val file = Source.fromFile(movieFile)
    moviesMap = file
      .getLines()
      .filter(!_.startsWith("ID"))
      .map(_.split(","))
      .filter(_.length == 3)
      .map(array => array(0).toInt -> array(2))
      .toMap
    file.close()
  }

  override def map(value: MovieRatingAnomaly): MovieRatingAnomalyWithTitle =
    MovieRatingAnomalyWithTitle(value.windowStart, value.movieId, moviesMap.get(value.movieId.toInt).orNull, value.ratingCount, value.ratingMean)
}
