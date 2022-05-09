package com.cherit.models

import java.util.Date

case class MovieRating(date: Date, movieId: Int, userId: String, rate: Int)
