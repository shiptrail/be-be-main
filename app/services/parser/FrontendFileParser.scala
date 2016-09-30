package services.parser

import java.io.InputStream

import io.github.karols.units.SI.Short._
import io.github.karols.units._
import models.TrackPoint
import models.TrackPoint.Annotation
import org.json4s._

import scala.collection.immutable.ListMap
import scala.collection.mutable.ListBuffer


object FrontendFileParser extends GpsTrackParser {

  override def parse(inputStream: InputStream): Iterator[TrackPoint] = {

    implicit val formats = DefaultFormats
    val tracks = org.json4s.jackson.JsonMethods.parse(inputStream).extract[Tracks]
    val allTracks = ListBuffer[TrackPoint]()

    for (track <- tracks.tracks) {
      var currentTrack: Map[IntU[ms], TrackPoint] = Map()
      for ((coordinate, i) <- track.coordinates.zipWithIndex) {
        val trackPoint = TrackPoint(coordinate.apply(0), coordinate.apply(1), coordinate.apply(2).toLong.of[ms])
        currentTrack = currentTrack.+((trackPoint.timestamp, trackPoint))
      }
      for (event <- track.events) {
        for ((coordinate, i) <- event.coordinates.zipWithIndex) {
          val trackPoint = TrackPoint(coordinate.apply(0),
            coordinate.apply(1), coordinate.apply(2).toLong.of[ms], None, List(), List(), List(), List(), findAnnotation(event.`type`, i, coordinate.apply(2).toLong.of[ms]))
          currentTrack = currentTrack.-(trackPoint.timestamp)
          currentTrack = currentTrack.+((trackPoint.timestamp, trackPoint))
        }
      }
      allTracks ++= ListMap(currentTrack.toSeq.sortBy(_._1): _*).values
    }
    allTracks.toIterator
  }

  case class Tracks(tracks: List[Track])

  case class Track(id: String, coordinates: Array[Array[Double]], events: Array[Event])

  case class Event(`type`: String, coordinates: Array[Array[Double]], description: String)


  def findAnnotation(typeOfEvent: String, positionOfCoordinate: Int, offset: IntU[ms]): Seq[Annotation] = {
    if (typeOfEvent.contentEquals("WENDE")) {
      positionOfCoordinate match {
        case 0 => Seq(TrackPoint.Annotation.apply(TrackPoint.AnnotationValue.StartJibe, offset))
        case 1 => Seq(TrackPoint.Annotation.apply(TrackPoint.AnnotationValue.MidJibe, offset))
        case 2 => Seq(TrackPoint.Annotation.apply(TrackPoint.AnnotationValue.EndJibe, offset))
      }
    } else {
      Seq(TrackPoint.Annotation.apply(TrackPoint.AnnotationValue.UnknownAnnotation, offset))
    }
  }

}
