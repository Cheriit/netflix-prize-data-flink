package com.cherit.models

case class MovieRatingAggregatedWithTitle(movieId: String, title: String, ratingCount: Int, ratingSum: Int, uniqueRatingCount: Int)
