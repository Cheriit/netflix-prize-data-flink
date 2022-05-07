package com.cherit.models

case class MovieRatingAnomaly(windowStart: String, windowStop: String, movieId: String, ratingCount: Long, ratingMean: Float)
