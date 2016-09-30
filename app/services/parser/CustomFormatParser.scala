package services
import java.io.InputStream

import models.TrackPoint
import play.api.libs.json.Json
import services.parser.GpsTrackParser

import scala.io.Source

/**
  * Copied from influx-simulator
  */
object CustomFormatParser extends GpsTrackParser {
  override def parse(inputStream: InputStream): Iterator[TrackPoint] = {
    val lines = Source.fromInputStream(inputStream).getLines()
    for {
      line: String <- lines
      trackPoint <- Json.parse(line).asOpt[TrackPoint]
    } yield trackPoint
  }
}
