package controllers

import java.util.UUID
import javax.inject._

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import models.TrackPoint
import play.api.libs.EventSource
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.{JsonPatchService, TrackService}

@Singleton
class FrontendController @Inject()(
    trackService: TrackService[TrackPoint])(implicit mat: Materializer)
    extends Controller {

  val jsonPatchService = new JsonPatchService()

  def allTracks() = Action { request =>
    val tracks: Source[JsValue, NotUsed] =
      trackService.allTracks.map((jsonPatchService.toJsonEvent _).tupled)

    Ok.chunked(tracks via EventSource.flow).as("text/event-stream")
  }
}
