package com.cherit.models

case class MovieRatingAccumulator(movieId: Int, ratingCount: Long, ratingSum: Long, ratingUsers: Set[String])
