package models
import io.github.karols.units.SI.Short._
import io.github.karols.units._
import models.TrackPoint.{Accelerometer, Compass, GpsMeta, Orientation}
import models.UnitHelper._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, _}

case class TrackPoint(lat: Double,
                      lng: Double,
                      timestamp: IntU[ms],
                      ele: Option[Double],
                      gpsMeta: Seq[GpsMeta],
                      compass: Seq[Compass],
                      accelerometer: Seq[Accelerometer],
                      orientation: Seq[Orientation])

object TrackPoint {

  case class Compass(deg: Double, toffset: Int)
  case class Accelerometer(x: DoubleU[m / (s ^ _2)],
                           y: DoubleU[m / (s ^ _2)],
                           z: DoubleU[m / (s ^ _2)],
                           toffset: IntU[ms])
  case class Orientation(azimuth: Double,
                         pitch: Double,
                         roll: Double,
                         toffset: IntU[ms])
  case class GpsMeta(accuracy: Double, satCount: Int, toffset: IntU[ms])

  implicit val gpsMetaFormat = Json.format[GpsMeta]
  implicit val compassFormat = Json.format[Compass]
  implicit val accelerometerFormat = Json.format[Accelerometer]
  implicit val orientationFormat = Json.format[Orientation]

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
        (JsPath \ "timestamp").format[IntU[ms]] and
        (JsPath \ "ele").formatNullable[Double] and
        optionalSeq[GpsMeta](JsPath \ "gpsMeta") and
        optionalSeq[Compass](JsPath \ "compass") and
        optionalSeq[Accelerometer](JsPath \ "accelerometer") and
        optionalSeq[Orientation](JsPath \ "orientation")
    )(TrackPoint.apply, unlift(TrackPoint.unapply))
  }
}
