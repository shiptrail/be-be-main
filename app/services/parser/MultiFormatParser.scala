package services

import java.io.File

import models.TrackPoint
import services.parser.{GpsTrackParser, GpxParser}

/**
  * Copied from influx-simulator
  */
object MultiFormatParser {
  val parserForExtension = Map(
    "gpx" -> GpxParser,
    "cgps" -> CustomFormatParser
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
