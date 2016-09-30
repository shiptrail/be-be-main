package services.parser

import java.io.InputStream

import io.github.karols.units.SI.Short._
import io.github.karols.units._
import models.TrackPoint
import models.TrackPoint.{Accelerometer, AnnotationValue, Compass, GpsMeta, Orientation}
import org.json4s.DefaultFormats

import scala.collection.mutable.ListBuffer

object AndroidFileParser extends GpsTrackParser {

  override def parse(inputStream: InputStream): Iterator[TrackPoint] = {
    implicit val formats = DefaultFormats
    val arrayOfUpdates = org.json4s.jackson.JsonMethods.parse(inputStream).extract[Array[Update]]
    val list = ListBuffer[TrackPoint]()
    for (update <- arrayOfUpdates) {
      val trackPoint = TrackPoint(
        update.lat,
        update.lng,
        update.timestamp.of[ms],
        Some(update.ele),
        update.gpsMeta,
        update.compass,
        update.accelerometer,
        update.orientation,
        annotation = {
          val annotationList = ListBuffer[TrackPoint.Annotation]()
          for (annotation <- update.annotation) {
            annotationList += TrackPoint.Annotation.apply(
              AnnotationValue withName annotation.`type`, (annotation.toffset).of[ms])
          }
          annotationList
        }
      )
      list += trackPoint
    }
    list.toIterator
  }

  case class Update(accelerometer: Array[Accelerometer],
                    annotation: Array[AnnotationOfUpdate],
                    compass: Array[Compass],
                    ele: Double,
                    gpsMeta: Array[GpsMeta],
                    lat: Double,
                    lng: Double,
                    orientation: Array[Orientation],
                    timestamp: Long)

  case class AnnotationOfUpdate(`type`: String, toffset: Long)

}
