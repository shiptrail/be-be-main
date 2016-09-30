package services.parser

import java.io.{File, FileInputStream, InputStream}

import models.TrackPoint

/**
  * Copied from influx-simulator
  */
trait GpsTrackParser {
  def parse(fileName: File): Iterator[TrackPoint] =
    parse(new FileInputStream(fileName))

  def parse(inputStream: InputStream): Iterator[TrackPoint]
}
