package controllers

import java.util.UUID
import javax.inject._

import akka.stream.Materializer
import models.TrackPoint
import play.api.mvc._
import services.TrackService

@Singleton
class BackendController @Inject()(
    trackService: TrackService[TrackPoint])(implicit mat: Materializer)
    extends Controller {

  def send2(device: UUID) = Action(BodyParsers.parse.json[List[TrackPoint]]) {
    request =>
      println(s"Got ${request.body.length} elements")

      request.body.foreach(trackService.consume)

      Ok
  }
}
