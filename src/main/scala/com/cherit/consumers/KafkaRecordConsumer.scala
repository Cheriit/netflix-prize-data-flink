package com.cherit.consumers

import org.apache.kafka.clients.consumer.KafkaConsumer

import java.time.Duration
import java.util.Collections.singletonList
import java.util.{NoSuchElementException, Properties}
import scala.collection.JavaConverters.iterableAsScalaIterableConverter

object KafkaRecordConsumer extends App {
  if (args.length != 3)
    throw new NoSuchElementException

  val properties = new Properties();
  properties.put("bootstrap.servers", args(0))
  properties.put("group.id", args(1))
  properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val consumer = new KafkaConsumer[String, String](properties)
  consumer.subscribe(singletonList(args(2)))
  while (true) {
    val results = consumer.poll(Duration.ofSeconds(60)).asScala
    results.foreach( data => println(data.value())
    )
  }
  consumer.close()
}
