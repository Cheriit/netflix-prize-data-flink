package com.cherit

import com.cherit.models.MovieRating

object Helpers {
  def getTimestamp[T](t: T): Long = {
    t match {
      case movieRating: MovieRating => movieRating.date.getTime
    }
  }
}
