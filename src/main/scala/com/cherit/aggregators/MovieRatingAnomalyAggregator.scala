package com.cherit.aggregators

import com.cherit.models.{MovieRating, MovieRatingAnomalyAccumulator, MovieRatingAnomalyAggregated}
import org.apache.flink.api.common.functions.AggregateFunction

class MovieRatingAnomalyAggregator extends AggregateFunction[MovieRating, MovieRatingAnomalyAccumulator, MovieRatingAnomalyAggregated] {
  override def createAccumulator(): MovieRatingAnomalyAccumulator = MovieRatingAnomalyAccumulator(0, 0, 0)

  override def add(in: MovieRating, acc: MovieRatingAnomalyAccumulator): MovieRatingAnomalyAccumulator = MovieRatingAnomalyAccumulator(in.movieId, acc.ratingCount + 1, acc.ratingSum + in.rate)

  override def getResult(acc: MovieRatingAnomalyAccumulator): MovieRatingAnomalyAggregated = MovieRatingAnomalyAggregated(acc.movieId, acc.ratingCount, acc.ratingSum/acc.ratingCount)

  override def merge(acc: MovieRatingAnomalyAccumulator, acc1: MovieRatingAnomalyAccumulator): MovieRatingAnomalyAccumulator = MovieRatingAnomalyAccumulator(acc.movieId, acc.ratingCount + acc1.ratingCount, acc.ratingSum + acc1.ratingSum)
}
