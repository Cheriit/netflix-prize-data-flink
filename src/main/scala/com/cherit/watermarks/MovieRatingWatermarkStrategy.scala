package com.cherit.watermarks

import org.apache.flink.api.common.eventtime.{TimestampAssignerSupplier, WatermarkGenerator, WatermarkGeneratorSupplier, WatermarkStrategy}

class MovieRatingWatermarkStrategy[T] extends WatermarkStrategy[T] {
  override def createTimestampAssigner(context: TimestampAssignerSupplier.Context) = new MovieRatingTimestampAssigner()

  override def createWatermarkGenerator(context: WatermarkGeneratorSupplier.Context): WatermarkGenerator[T] = new MovieRatingWatermarkGenerator()
}
