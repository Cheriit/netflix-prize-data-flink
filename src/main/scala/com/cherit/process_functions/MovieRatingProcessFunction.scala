package com.cherit.process_functions

import com.cherit.models.{MovieRatingAggregated, MovieRatingResult}
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

class MovieRatingProcessFunction extends ProcessWindowFunction[MovieRatingAggregated, MovieRatingResult, String, TimeWindow]{
  override def process(key: String, context: Context, elements: Iterable[MovieRatingAggregated], out: Collector[MovieRatingResult]): Unit =
    elements.foreach(rating => out.collect(MovieRatingResult(context.window.getStart, rating.movieId, rating.ratingCount, rating.ratingSum, rating.uniqueRatingCount)))

}
