package com.cherit.processing_engine

import com.cherit.Helpers
import org.apache.flink.api.common.eventtime.TimestampAssigner

class MovieRatingTimestampAssigner[T] extends TimestampAssigner[T] {
  override def extractTimestamp(t: T, l: Long): Long = Helpers.getTimestamp[T](t)
}
