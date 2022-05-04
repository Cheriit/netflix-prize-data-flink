package com.cherit.processing_engine

import com.cherit.models.{MovieRating, MovieRatingAnomaly}
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

class MovieRatingAnomalyWindowFunction extends ProcessWindowFunction[MovieRatingAnomaly, MovieRatingAnomaly, String, TimeWindow]{
  override def process(key: String, context: Context, elements: Iterable[MovieRatingAnomaly], out: Collector[MovieRatingAnomaly]): Unit = {
    elements.foreach(anomaly => out.collect(MovieRatingAnomaly(context.window.getStart.toString, context.window.getEnd.toString, anomaly.movieId, anomaly.ratingsCount, anomaly.ratingsMean)))
  }
}
