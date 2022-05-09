package com.cherit.models

case class MovieRatingAnomalyWithTitle(windowStart: String, windowStop: String, movieId: Int, movieTitle: String, ratingCount: Long, ratingMean: Float)
