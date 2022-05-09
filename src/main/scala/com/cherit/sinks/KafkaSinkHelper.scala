package com.cherit.sinks

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.connector.base.DeliveryGuarantee
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}

object KafkaSinkHelper {
  def get(bootstrapServers: String, topic: String): KafkaSink[String] =
    KafkaSink.builder[String]()
    .setBootstrapServers(bootstrapServers)
    .setRecordSerializer(KafkaRecordSerializationSchema
    .builder()
    .setTopic(topic)
    .setValueSerializationSchema(new SimpleStringSchema())
    .build())
    .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
    .build()
}
