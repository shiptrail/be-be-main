package models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, _}

case class TrackPoint(lat: Double,
                      lng: Double,
                      timestamp: Int,
                      ele: Option[Double],
                      gpsMeta: Option[Seq[GpsMeta]],
                      compass: Option[Seq[Compass]],
                      accelerometer: Option[Seq[Accelerometer]],
                      orientation: Option[Seq[Orientation]])

case class Compass(deg: Double, toffset: Int)

case class Accelerometer(x: Double, y: Double, z: Double, toffset: Int)

case class Orientation(
    azimuth: Double, pitch: Double, roll: Double, toffset: Int)

case class GpsMeta(accuracy: Double, satCount: Int, toffset: Int)

object TrackPoint {
  implicit val compassFormat = Json.format[Compass]
  implicit val accelerometerFormat = Json.format[Accelerometer]
  implicit val orientationFormat = Json.format[Orientation]
  implicit val gpsMetaFormat = Json.format[GpsMeta]

  implicit val trackPointFormat: Format[TrackPoint] = {
    (
        (JsPath \ "lat").format[Double](min(-90.0) keepAnd max(90.0)) and
        (JsPath \ "lng").format[Double](min(-180.0) keepAnd max(180.0)) and
        (JsPath \ "timestamp").format[Int](min(0)) and
        (JsPath \ "ele").formatNullable[Double] and
        (JsPath \ "gpsMeta").formatNullable[Seq[GpsMeta]] and
        (JsPath \ "compass").formatNullable[Seq[Compass]] and
        (JsPath \ "accelerometer").formatNullable[Seq[Accelerometer]] and
        (JsPath \ "orientation").formatNullable[Seq[Orientation]]
    )(TrackPoint.apply, unlift(TrackPoint.unapply))
  }
}
