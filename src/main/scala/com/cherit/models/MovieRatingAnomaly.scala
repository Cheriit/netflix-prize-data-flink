package com.cherit.models

case class MovieRatingAnomaly(windowStart: String, windowStop: String, movieId: String, ratingsCount: Int, ratingsMean: Long)
