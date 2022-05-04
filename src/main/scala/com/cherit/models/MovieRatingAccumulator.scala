package com.cherit.models

case class MovieRatingAccumulator(movieId: String, ratingCount: Int, ratingSum: Long, ratingUsers: Set[String])
