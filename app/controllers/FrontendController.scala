package controllers

import java.util.UUID
import javax.inject._

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import models.TrackPoint
import play.api.libs.EventSource
import play.api.libs.json.{JsValue, Json, Writes}
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

    if (liveTrackUuids.isEmpty)
      Ok(toJsonRecord(liveTrackUuids))
    else
      Ok(toJsonRecord(liveTrackUuids))
  }

  def toJsonRecord(devices: Seq[UUID]) = {
    val devicesAsTrack = devices.map(toJsonTrack)
    Json.obj(
        "records" ->
        Json.arr(
            Json.obj(
                "id" -> "32",
                "name" -> "Live test",
                "date" -> "now",
                "length" -> "1nm",
                "location" -> "Berlin",
                "tracks" -> devicesAsTrack
            )
        )
    )
  }

  def toJsonTrack(device: UUID): JsValue = {
    Json.obj(
        "id" -> device.toString.filter((c) => c != '-'),
        "shipName" -> "ship"
    )
  }

  def toJsonEvent(device: UUID, trackPoint: TrackPoint) = {
    Json.arr(
        Json.obj(
            "trackId" -> device.toString.filter((c) => c != '-'),
            "type" -> "coordinates",
            "coordinates" -> Json.arr(
                Json.arr(
                    trackPoint.lng,
                    trackPoint.lat,
                    trackPoint.timestamp.value
                )
            )
        ))
  }
}
