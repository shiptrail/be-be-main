package services.parser

import java.text.SimpleDateFormat

import scala.util.{Success, Try}

/**
  * Copied from influx-simulator
  */
object StringUtils {
  private val formats =
    List(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
      "yyyy-MM-dd'T'HH:mm:ss'Z'")
      .map(format => new SimpleDateFormat(format))

  def toUnixTime(str: String): Option[Int] = {
    formats
      .map { format => Try{format.parse(str)} }
      .collectFirst{ case Success(parsed) => parsed }
      .map { _.getTime / 1000 }
      .map { _.toInt }
  }

  def toDouble(str: String): Option[Double] =
    Try { str.toDouble }.toOption

  def toInt(str: String): Option[Int] =
    Try { str.toInt }.toOption
}
