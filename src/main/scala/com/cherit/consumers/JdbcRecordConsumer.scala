package com.cherit.consumers

import java.sql.{Connection, DriverManager}
import java.util.NoSuchElementException

object JdbcRecordConsumer extends App {
  if (args.length != 4)
    throw new NoSuchElementException

  var connection: Connection = _
  try {
    Class.forName(args(1))
    connection = DriverManager.getConnection(args(0), args(2), args(3))
    val statement = connection.createStatement
    while(true) {
      val result = statement.executeQuery("SELECT * FROM ratings ORDER BY window_start DESC LIMIT 50")
      print("\u001b[2J")
      while(result.next) {
        val windowStart = result.getDate("window_start").toLocalDate
        val movieId = result.getString("movie_id")
        val title = result.getString("title")
        val ratingCount = result.getInt("rating_count")
        val ratingSum = result.getString("rating_sum")
        val uniqueRatingCount = result.getInt("unique_rating_count")
        val dateFrom = s"${windowStart.getYear}-${windowStart.getMonth}-${windowStart.getDayOfMonth}"
        windowStart.plusDays(30)
        val dateTo = s"${windowStart.getYear}-${windowStart.getMonth}-${windowStart.getDayOfMonth}"
        println(s"$dateFrom - $dateTo \t\t $title($movieId) \t $ratingCount \t $ratingSum \t $uniqueRatingCount")
      }
      Thread.sleep(10000)
    }
  } catch {
    case e: Exception => e.printStackTrace()
  }
  connection.close()
}
