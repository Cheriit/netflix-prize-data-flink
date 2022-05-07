package com.cherit.aggregators

import com.cherit.models.{MovieRating, MovieRatingAccumulator, MovieRatingAggregated}
import org.apache.flink.api.common.functions.AggregateFunction

class MovieRatingAggregator extends AggregateFunction[MovieRating, MovieRatingAccumulator, MovieRatingAggregated] {
  override def createAccumulator(): MovieRatingAccumulator = MovieRatingAccumulator("99999999999999999999", 0, 0, Set())

  override def add(in: MovieRating, acc: MovieRatingAccumulator): MovieRatingAccumulator = MovieRatingAccumulator(in.movieId, acc.ratingCount + 1, acc.ratingSum + in.rate, acc.ratingUsers + in.userId)

  override def getResult(acc: MovieRatingAccumulator): MovieRatingAggregated = MovieRatingAggregated(acc.movieId, acc.ratingCount, acc.ratingSum, acc.ratingUsers.count(_ => true))

  override def merge(acc: MovieRatingAccumulator, acc1: MovieRatingAccumulator): MovieRatingAccumulator = MovieRatingAccumulator(acc.movieId, acc.ratingCount + acc1.ratingCount, acc.ratingSum + acc1.ratingSum, acc.ratingUsers ++ acc1.ratingUsers)
}
