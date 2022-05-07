package com.cherit.models

case class MovieRatingAnomalyWithTitle(windowStart: String, windowStop: String, movieTitle: String, ratingCount: Long, ratingMean: Float)
