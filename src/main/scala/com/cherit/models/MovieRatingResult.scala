package com.cherit.models

import java.sql.Timestamp

case class MovieRatingResult(windowStart: Long, movieId: Int, ratingCount: Long, ratingSum: Long, uniqueRatingCount: Long)
