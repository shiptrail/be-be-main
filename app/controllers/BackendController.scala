package controllers

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

  case class ClientUpdate(id: Int,
                          lat: Double,
                          lng: Double,
                          ele: Double,
                          heading: Double,
                          timestamp: Int)

  implicit val clientUpdate: Reads[ClientUpdate] = (
      (JsPath \ "id").read[Int] and
      (JsPath \ "lat").read[Double](min(-90.0) keepAnd max(90.0)) and
      (JsPath \ "lng").read[Double](min(-180.0) keepAnd max(180.0)) and
      (JsPath \ "ele").read[Double](min(-1000.0) keepAnd max(8000.0)) and
      (JsPath \ "heading").read[Double](min(0.0) keepAnd max(360.0)) and
      (JsPath \ "timestamp").read[Int](min(0))
  )(ClientUpdate.apply _)

  def send = Action(BodyParsers.parse.json[Seq[ClientUpdate]]) { request =>
    val coordinates = request.body

    Ok
  }
}
