package com.cherit.rich_functions

import com.cherit.models.{MovieRatingResult, MovieRatingResultWithTitle}
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration

import scala.io.Source

class MovieResultRichFunction extends RichMapFunction[MovieRatingResult, MovieRatingResultWithTitle]{
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

  override def map(value: MovieRatingResult): MovieRatingResultWithTitle =
    MovieRatingResultWithTitle(value.windowStart, value.movieId, moviesMap.get(value.movieId.toInt).orNull, value.ratingCount, value.ratingSum ,value.uniqueRatingCount)
}
