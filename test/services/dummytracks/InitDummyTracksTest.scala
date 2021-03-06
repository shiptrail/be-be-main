package services.dummytracks

import java.io.File
import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import models.TrackPoint
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import services.{MultiFormatParser, TrackServiceImpl}

import scala.concurrent.ExecutionContext.Implicits.global

class InitDummyTracksTest extends FlatSpec
  with Matchers
  with ScalaFutures
  with MockitoSugar {

  implicit val sys = ActorSystem()
  implicit val mat = ActorMaterializer()

  val someId = UUID.randomUUID()

  "Init track" should "load all 3 device id's into knownDummyUuids" in {
    val hashSetMock = mock[scala.collection.mutable.HashSet[UUID]]
    val trackService = new TrackServiceImpl[TrackPoint] {
      override val knownDummyUuids = hashSetMock
    }

    val classUnderTest = new LoadInitTracks(trackService)

    val trackPointIterator = MultiFormatParser.parse(new File("./test/services/dummytracks/minimal.gpx"))
    classUnderTest.loadDummyTrack(trackPointIterator, someId)
    verify(trackService.knownDummyUuids, times(3)).+=(any())
  }

}
