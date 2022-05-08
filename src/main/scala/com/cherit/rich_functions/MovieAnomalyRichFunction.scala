package com.cherit.rich_functions

import com.cherit.models.{MovieRatingAnomaly, MovieRatingAnomalyWithTitle}
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration

import java.nio.charset.CodingErrorAction
import scala.io.{Codec, Source}

class MovieAnomalyRichFunction extends RichMapFunction[MovieRatingAnomaly, MovieRatingAnomalyWithTitle]{
  var moviesMap: Map[Int, String] = _

  override def open(parameters: Configuration): Unit = {
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

    val movieFile = getRuntimeContext.getDistributedCache.getFile("moviesFile")
    val file = Source.fromFile(movieFile)
    moviesMap = file
      .getLines()
      .filter(!_.startsWith("ID"))
      .map(_.split(",", 3))
      .filter(_.length == 3)
      .map(array => array(0).toInt -> array(2))
      .toMap
    file.close()
  }

  override def map(value: MovieRatingAnomaly): MovieRatingAnomalyWithTitle =
    MovieRatingAnomalyWithTitle(value.windowStart, value.movieId, moviesMap.get(value.movieId.toInt).orNull, value.ratingCount, value.ratingMean)
}
