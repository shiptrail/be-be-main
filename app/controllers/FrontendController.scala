package controllers

import java.text.SimpleDateFormat
import java.util.{Date, UUID}
import javax.inject._

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import models.TrackPoint
import play.api.libs.EventSource
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.TrackService

class FrontendController @Inject()(
                                    trackService: TrackService[TrackPoint])(implicit mat: Materializer)
  extends Controller {

  def allTracks() = Action { request =>
    val tracks: Source[JsValue, NotUsed] =
      trackService.allTracks.map((toJsonEvent _).tupled)

    Ok.chunked(tracks via EventSource.flow).as("text/event-stream")
  }

  def trackMetaData() = Action { request =>
    val liveTrackUuids: Seq[UUID] = trackService.allDevices
    val offlineTrackUuids: Seq[UUID] = trackService.allDummyDevices

    Ok(toJsonRecord(liveTrackUuids, offlineTrackUuids))
  }

  def toJsonRecord(devices: Seq[UUID], offlineDevices: Seq[UUID]) = {
    val devicesAsTrack = devices.map(toJsonTrackForLiveData)
    val offlineDevicesAsTrack = offlineDevices.map(toJsonTrack)
    var records = Json.arr()
    val df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
    records = records.+:(
      Json.obj(
        "id" -> "0",
        "name" -> "Live test",
        "date" -> "now",
        "length" -> "0",
        "location" -> "Berlin",
        "tracks" -> devicesAsTrack
      )
    )
    for ((device_track_tuple, id) <- offlineDevicesAsTrack.zipWithIndex) {
      records = records.+:(
        Json.obj(
          "id" -> (id + 1),
          "name" -> "Track",
          "date" -> df.format(new Date(trackService
            .getTimestampOfDevice(device_track_tuple._1)
            .value)),
          "length" -> "0",
          "location" -> "Berlin",
          "tracks" -> Json.arr(device_track_tuple._2)
        ))
    }
    Json.obj("records" -> records)
  }

  def toJsonTrackForLiveData(device: UUID): JsValue = {
    Json.obj(
      "id" -> device.toString.filter((c) => c != '-'),
      "shipName" -> "ship"
    )
  }

  def toJsonTrack(device: UUID): (UUID, JsValue) = {
    (device,
      Json.obj(
        "id" -> device.toString.filter((c) => c != '-'),
        "shipName" -> "ship"
      ))
  }

  def toJsonEvent(device: UUID, trackPoint: TrackPoint) = {
    Json.arr(
      Json.obj(
        "trackId" -> device.toString.filter((c) => c != '-'),
        "type" -> "coordinates",
        "coordinates" -> Json.arr(
          Json.arr(
            trackPoint.lat,
            trackPoint.lng,
            trackPoint.timestamp.value
          )
        ),
        "events" -> toProcessedEvent(trackPoint)
      ))
  }

  def toProcessedEvent(trackPoint: TrackPoint) = {
    trackPoint.annotation.map((annotation) => {
      Json.obj(
        "type" -> annotation.`type`,
        "coordinates" -> Json.arr(
          trackPoint.lng,
          trackPoint.lat,
          (trackPoint.timestamp + annotation.toffset).value
        ),
        "description" -> annotation.`type`
      )
    })
  }
}
