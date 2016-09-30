package services.parser

import java.io.{File, FileInputStream}

import models.TrackPoint
import models.TrackPoint.{Accelerometer, Annotation, AnnotationValue, Compass, GpsMeta, Orientation}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import io.github.karols.units.SI.Short._
import io.github.karols.units._
import models.UnitHelper._
import services.CustomFormatParser

class CGPSFileParserTest extends FlatSpec
  with Matchers
  with ScalaFutures
  with MockitoSugar {

  val expectedResultsMinimalAndroidAppFile = List(
    TrackPoint(52.518734, 13.321354, 1475681027180L.of[ms], Some(100.0), Seq(GpsMeta(7.0, 0, 0.of[ms])), Seq(Compass(153.6848, 931)), Seq(Accelerometer(-1.2443863.of[m / (s ^ _2)], 4.5406036.of[m / (s ^ _2)], 13.64695.of[m / (s ^ _2)], 931.of[ms])), Seq(Orientation(153.6848, -18.332338, 5.2100616, 931.of[ms])), List(Annotation(AnnotationValue withName "START_JIBE", 11806.of[ms]))),
    TrackPoint(52.518574, 13.321948, 1475681046174L.of[ms], Some(84.0), Seq(GpsMeta(9.0, 0, 0.of[ms])), Seq(Compass(170.18939, 280)), Seq(Accelerometer(-0.48123455.of[m / (s ^ _2)], 2.4582467.of[m / (s ^ _2)], 9.826403.of[m / (s ^ _2)], 280.of[ms])), Seq(Orientation(170.18939, -14.029127, 2.8037417, 280.of[ms])), List()),
    TrackPoint(52.518394, 13.321941, 1475681056187L.of[ms], Some(92.0), Seq(GpsMeta(9.0, 0, 0.of[ms])), Seq(Compass(156.7807, 1018)), Seq(Accelerometer(1.7262194.of[m / (s ^ _2)], 4.995502.of[m / (s ^ _2)], 12.195465.of[m / (s ^ _2)], 1018.of[ms])), Seq(Orientation(156.7807, -22.076334, -8.056468, 1018.of[ms])), List())
  )


  "A parsed minimal CustomFormatFile file with three Trackpoint elements" should "contain three TrackPoint objects" in {
    val fileInputStream = new FileInputStream(new File("./test/services/parser/testfiles/androidTestFile.json"))
    val trackPoints = CustomFormatParser.parse(fileInputStream)
    trackPoints should have length 3
  }

  it should "contain the expected TrackPoints" in {
    val fileInputStream = new FileInputStream(new File("./test/services/parser/testfiles/androidTestFile.json"))
    val trackPoints = CustomFormatParser.parse(fileInputStream)
    trackPoints.toList should be(expectedResultsMinimalAndroidAppFile)
  }


}
