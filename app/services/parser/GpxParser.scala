package services.parser

import java.io.InputStream

import io.github.karols.units.SI.Short._
import io.github.karols.units._
import models.TrackPoint
import models.TrackPoint.Accelerometer
import models.UnitHelper._

import scala.language.implicitConversions

/**
  * Copied from influx-simulator
  */
object GpxParser extends GpsTrackParser with XmlBasedParser {
  def parse(is: InputStream): Iterator[TrackPoint] = {
    val nodes = getElementsWithNameFromInputStream("trkpt", is)

    for {
      xmlNode <- nodes
      lat <- whenDefined(xmlNode \ "@lat").flatMap(StringUtils.toDouble)
      lng <- whenDefined(xmlNode \ "@lon").flatMap(StringUtils.toDouble)
      time <- whenDefined(xmlNode \ "time").flatMap(StringUtils.toUnixTime)
      ele = whenDefined(xmlNode \ "ele")
        .flatMap(StringUtils.toDouble)
        .getOrElse(0.0)
      accel = (xmlNode \ "extensions" \ "AccelerationExtension" \ "accel").flatMap {
        accelEntry =>
          for {
            x <- whenDefined(accelEntry \ "@x").flatMap(StringUtils.toDouble)
            y <- whenDefined(accelEntry \ "@y").flatMap(StringUtils.toDouble)
            z <- whenDefined(accelEntry \ "@z").flatMap(StringUtils.toDouble)
            offset <- whenDefined(accelEntry \ "@offset")
                       .flatMap(StringUtils.toInt)
          } yield
            Accelerometer(x.of[m / (s ^ _2)],
                          y.of[m / (s ^ _2)],
                          z.of[m / (s ^ _2)],
                          offset.of[ms])
      }
    } yield TrackPoint(lat, lng, time.of[ms], Some(ele), accelerometer = accel)
  }
}
