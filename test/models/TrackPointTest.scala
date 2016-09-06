package models

import io.github.karols.units.SI.Short.ms
import io.github.karols.units._
import models.TrackPoint.{Annotation, AnnotationValue}
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json

/**
  * Created by phiros on 10.09.16.
  */
class TrackPointTest extends FlatSpec with Matchers {
  "A TrackPoint with annotation" should "be able to be transformed into json" in {
    val expectedJson = Json.obj(
      "lat" -> 1,
      "lng" -> 1,
      "timestamp" -> 1,
      "gpsMeta" -> Json.arr(),
      "compass" -> Json.arr(),
      "accelerometer" -> Json.arr(),
      "orientation" -> Json.arr(),
      "annotation" -> Json.arr(
        Json.obj(
          "type" -> "START_JIBE",
          "toffset" -> 0
        )
      )
    )

    val trackPoint = TrackPoint(1,1,1.of[ms], annotation = Seq(Annotation(AnnotationValue.StartJibe, 0.of[ms])))
    val json = Json.toJson(trackPoint)
    json should be(expectedJson)
  }
}
