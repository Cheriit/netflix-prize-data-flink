package com.cherit.watermarks

import com.cherit.Helpers
import org.apache.flink.api.common.eventtime.{Watermark, WatermarkGenerator, WatermarkOutput}

class MovieRatingWatermarkGenerator[T] extends WatermarkGenerator[T] {

  val maxOutOfOrderness = 24*60*60*1000

  var currentMaxTimestamp: Long = _

  override def onEvent(t: T, l: Long, watermarkOutput: WatermarkOutput): Unit = {
    currentMaxTimestamp = math.max(Helpers.getTimestamp[T](t), currentMaxTimestamp)
  }

  override def onPeriodicEmit(watermarkOutput: WatermarkOutput): Unit = {
    watermarkOutput.emitWatermark(new Watermark(currentMaxTimestamp - maxOutOfOrderness - 1))
  }
}
