package models
import models.TrackPoint.{Accelerometer, Compass, GpsMeta, Orientation}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, _}

case class TrackPoint(lat: Double,
                      lng: Double,
                      timestamp: Int,
                      ele: Option[Double],
                      gpsMeta: Seq[GpsMeta],
                      compass: Seq[Compass],
                      accelerometer: Seq[Accelerometer],
                      orientation: Seq[Orientation])

object TrackPoint {
  case class Compass(deg: Double, toffset: Int)
  case class Accelerometer(x: Double, y: Double, z: Double, toffset: Int)
  case class Orientation(azimuth: Double,
                         pitch: Double,
                         roll: Double,
                         toffset: Int)
  case class GpsMeta(accuracy: Double, satCount: Int, toffset: Int)

  implicit val compassFormat = Json.format[Compass]
  implicit val accelerometerFormat = Json.format[Accelerometer]
  implicit val orientationFormat = Json.format[Orientation]
  implicit val gpsMetaFormat = Json.format[GpsMeta]

  def optionalSeq[T: Format](path: JsPath): OFormat[Seq[T]] =
    path
      .formatNullable[Seq[T]]
      .inmap(
        o => o.getOrElse(Seq.empty),
        s => Some(s)
      )

  implicit val trackPointFormat: Format[TrackPoint] = {
    (
      (JsPath \ "lat").format[Double](min(-90.0) keepAnd max(90.0)) and
        (JsPath \ "lng").format[Double](min(-180.0) keepAnd max(180.0)) and
        (JsPath \ "timestamp").format[Int](min(0)) and
        (JsPath \ "ele").formatNullable[Double] and
        optionalSeq[GpsMeta](JsPath \ "gpsMeta") and
        optionalSeq[Compass](JsPath \ "compass") and
        optionalSeq[Accelerometer](JsPath \ "accelerometer") and
        optionalSeq[Orientation](JsPath \ "orientation")
      )(TrackPoint.apply, unlift(TrackPoint.unapply))
  }
}