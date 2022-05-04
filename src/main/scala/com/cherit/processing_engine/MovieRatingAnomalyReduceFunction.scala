package com.cherit.processing_engine

import com.cherit.models.MovieRating
import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction

class MovieRatingAnomalyReduceFunction extends ReduceFunction[MovieRating]{
  override def reduce(t: MovieRating, t1: MovieRating): MovieRating = ???
}
