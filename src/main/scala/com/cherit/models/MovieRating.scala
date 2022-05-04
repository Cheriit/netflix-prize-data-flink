package com.cherit.models

import java.util.Date

case class MovieRating(date: Date, movieId: String, userId: String, rate: Int)
