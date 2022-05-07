package com.cherit.models

case class MovieRatingAccumulator(movieId: String, ratingCount: Long, ratingSum: Long, ratingUsers: Set[String])
