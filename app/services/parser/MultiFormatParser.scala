package services

import java.io.File

import models.TrackPoint
import services.parser.{AndroidFileParser, FrontendFileParser, GpsTrackParser, GpxParser}

/**
  * Copied from influx-simulator and edited
  */
object MultiFormatParser {

  val parserForExtension = Map(
    "gpx" -> GpxParser,
    "cgps" -> CustomFormatParser,
    "fps" -> FrontendFileParser,
    "json" -> AndroidFileParser
  )

  def parse(file: File): Iterator[TrackPoint] = {
    parserForFileName(file) match {
      case Some(parserForFile) => parserForFile.parse(file)
      case None =>
        System.err.println("Error: Unsupported file type!")
        Iterator.empty
    }
  }

  def parserForFileName(file: File): Option[GpsTrackParser] = {
    val fileExtension = file.getName.split('.').last
    parserForExtension.get(fileExtension)
  }
}
