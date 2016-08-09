package controllers

import java.util.UUID
import javax.inject._

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class BackendController @Inject() extends Controller {

  val clientUpdateV1: Reads[List[Unit]] = {
    implicit val singleElement: Reads[Unit] = (
        (JsPath \ "id").read[Int] and
        (JsPath \ "lat").read[Double](min(-90.0) keepAnd max(90.0)) and
        (JsPath \ "lng").read[Double](min(-180.0) keepAnd max(180.0)) and
        (JsPath \ "ele").read[Double](min(-1000.0) keepAnd max(8000.0)) and
        (JsPath \ "heading").read[Double](min(0.0) keepAnd max(360.0)) and
        (JsPath \ "timestamp").read[Int](min(0))
    ).tupled.map(_ => ())

    implicitly[Reads[List[Unit]]]
  }

  val clientUpdateV2: Reads[List[Unit]] = {
    implicit val singleElement: Reads[Unit] = (
        (JsPath \ "lat").read[Double](min(-90.0) keepAnd max(90.0)) and
        (JsPath \ "lng").read[Double](min(-180.0) keepAnd max(180.0)) and
        (JsPath \ "ele").read[Double](min(-1000.0) keepAnd max(8000.0)) and
        (JsPath \ "heading").read[Double](min(0.0) keepAnd max(360.0)) and
        (JsPath \ "timestamp").read[Int](min(0))
    ).tupled.map(_ => ())

    implicitly[Reads[List[Unit]]]
  }

  def send = Action(BodyParsers.parse.json(clientUpdateV1)) { request =>
    Ok
  }

  def send2(device: UUID) = Action(BodyParsers.parse.json(clientUpdateV2)) {
    request =>
      Ok
  }
}
