package services.parser

import java.io.{File, FileInputStream}

import io.github.karols.units.SI.Short._
import io.github.karols.units._
import models.TrackPoint
import models.TrackPoint.{Annotation, AnnotationValue}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}


class FrontendFileParserTest extends FlatSpec
  with Matchers
  with ScalaFutures
  with MockitoSugar {

  val expectedResultsMinimalFPS = List(
    TrackPoint(13.210308635607362, 52.50886609777808, 1639.of[ms], None, List(), List(), List(), List(), List(Annotation(AnnotationValue withName "START_JIBE", 1639.of[ms]))),
    TrackPoint(13.21028558537364, 52.508917981758714, 1640.of[ms], None, List(), List(), List(), List(), List(Annotation(AnnotationValue withName "MID_JIBE", 1640.of[ms]))),
    TrackPoint(13.210270497947931, 52.508938098326325, 1641.of[ms], None, List(), List(), List(), List(), List(Annotation(AnnotationValue withName "END_JIBE", 1641.of[ms])))
  )

  "A parsed minimal fps file with three Trackpoint elements" should "contain three TrackPoint objects" in {
    val fileInputStream = new FileInputStream(new File("./test/services/parser/testfiles/frontendFile.fps"))
    val trackPoints = FrontendFileParser.parse(fileInputStream)
    trackPoints should have length 3
  }

  it should "contain the expected TrackPoints" in {
    val fileInputStream = new FileInputStream(new File("./test/services/parser/testfiles/frontendFile.fps"))
    val trackPoints = FrontendFileParser.parse(fileInputStream)
    trackPoints.toList should be(expectedResultsMinimalFPS)
  }

}
