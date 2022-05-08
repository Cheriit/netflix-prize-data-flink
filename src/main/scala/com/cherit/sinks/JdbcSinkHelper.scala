package com.cherit.sinks

import org.apache.flink.connector.jdbc.{JdbcConnectionOptions, JdbcExecutionOptions, JdbcSink, JdbcStatementBuilder}
import org.apache.flink.streaming.api.functions.sink.SinkFunction

object JdbcSinkHelper {
  def get[T](statement: String, statementFunction: JdbcStatementBuilder[T], url: String, driverName: String, username: String, password: String): SinkFunction[T] = JdbcSink.sink(
    statement,
    statementFunction,
    JdbcExecutionOptions
      .builder()
      .withBatchSize(100)
      .withBatchIntervalMs(200)
      .withMaxRetries(5)
      .build(),
    new JdbcConnectionOptions
    .JdbcConnectionOptionsBuilder()
      .withUrl(url)
      .withDriverName(driverName)
      .withUsername(username)
      .withPassword(password)
      .build()
  )
}
