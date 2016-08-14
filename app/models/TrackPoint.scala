package models

import play.api.libs.json.{JsPath, Json, Reads}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class TrackPoint(lat: Double,
                      lng: Double,
                      ele: Double,
                      heading: Double,
                      timestamp: Int)

object TrackPoint {
  implicit val trackPointFormat: Format[TrackPoint] = {
    (
        (JsPath \ "lat").format[Double](min(-90.0) keepAnd max(90.0)) and
        (JsPath \ "lng").format[Double](min(-180.0) keepAnd max(180.0)) and
        (JsPath \ "ele").format[Double](min(-1000.0) keepAnd max(8000.0)) and
        (JsPath \ "heading").format[Double](min(0.0) keepAnd max(360.0)) and
        (JsPath \ "timestamp").format[Int](min(0))
    )(TrackPoint.apply(_, _, _, _, _), TrackPoint.unapply(_).get)
  }
}
