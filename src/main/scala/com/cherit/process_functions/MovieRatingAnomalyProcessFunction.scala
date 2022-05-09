package com.cherit.process_functions

import com.cherit.models.{MovieRatingAnomaly, MovieRatingAnomalyAggregated}
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

class MovieRatingAnomalyProcessFunction extends ProcessWindowFunction[MovieRatingAnomalyAggregated, MovieRatingAnomaly, Int, TimeWindow]{
  override def process(key: Int, context: Context, elements: Iterable[MovieRatingAnomalyAggregated], out: Collector[MovieRatingAnomaly]): Unit =
    elements.foreach(anomaly => out.collect(MovieRatingAnomaly(context.window.getStart.toString, context.window.getEnd.toString, anomaly.movieId, anomaly.ratingCount, anomaly.ratingMean)))

}
