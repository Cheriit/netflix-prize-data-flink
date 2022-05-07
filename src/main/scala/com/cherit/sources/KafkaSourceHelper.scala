package com.cherit.sources

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.kafka.clients.consumer.OffsetResetStrategy

import java.util.Collections.singletonList

object KafkaSourceHelper {
  def get(bootstrapServers: String, topic: String, groupId: String): KafkaSource[String] = KafkaSource
    .builder()
    .setBootstrapServers(bootstrapServers)
    .setTopics(singletonList(topic))
    .setGroupId(groupId)
    .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
    .setValueOnlyDeserializer(new SimpleStringSchema())
    .build()
}
