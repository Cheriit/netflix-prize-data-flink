package com.cherit.models

case class MovieRatingAnomaly(windowStart: String, windowStop: String, movieId: Int, ratingCount: Long, ratingMean: Float)
