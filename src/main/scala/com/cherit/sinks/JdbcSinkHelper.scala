package com.cherit.sinks

import com.cherit.models.MovieRatingResultWithTitle
import com.cherit.processing_engines.ProcessingEngine.args
import org.apache.flink.connector.jdbc.{JdbcConnectionOptions, JdbcExecutionOptions, JdbcSink, JdbcStatementBuilder}
import org.apache.flink.streaming.api.functions.sink.SinkFunction

import java.sql.Date

object JdbcSinkHelper {
  def get[T](statement: String, statementFunction: JdbcStatementBuilder[T], url: String, driverName: String, username: String, password: String): SinkFunction[T] = JdbcSink.sink(
    statement,
    statementFunction,
    JdbcExecutionOptions
      .builder()
      .withBatchSize(1000)
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
