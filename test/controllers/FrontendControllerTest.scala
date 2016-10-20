package controllers

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import io.github.karols.units.SI.Short.ms
import io.github.karols.units._
import models.TrackPoint
import org.scalatest.mock.MockitoSugar
import play.api.test.Helpers._
import play.api.test._
import services.TrackService
import org.mockito.Mockito._
import org.scalatest.{FlatSpec, Matchers}

class FrontendControllerTest extends FlatSpec with Matchers with MockitoSugar {

  implicit val sys = ActorSystem()
  implicit val mat = ActorMaterializer()

  "Frontend Controller" should "pass data from the track service" in {
    val trackServiceMock = mock[TrackService[TrackPoint]]

    val someUUID = UUID.randomUUID()

    when(trackServiceMock.allTracks)
      .thenReturn(Source(someUUID -> TrackPoint(10, 10, 10.of[ms]) :: Nil))
    val controller = new FrontendController(trackServiceMock)

    val validRequest = FakeRequest(
        Helpers.POST,
        controllers.routes.FrontendController.allTracks().url
    )

    val result = controller.allTracks()(validRequest)

    status(result) should be(OK)
    contentAsString(result) should include(someUUID.toString.replaceAll("-", ""))
  }

  it should "deliver track meta data for the frontend" in {
    val trackServiceMock = mock[TrackService[TrackPoint]]

    val someUUID = UUID.randomUUID()
    when(trackServiceMock.allTracks)
      .thenReturn(Source(someUUID -> TrackPoint(10, 10, 10.of[ms]) :: Nil))
    when(trackServiceMock.allDevices)
      .thenReturn(Seq(someUUID))

    val controller = new FrontendController(trackServiceMock)
    val result = controller.trackMetaData()(FakeRequest())

    status(result) should be(OK)
    val contentString = contentAsString(result)
    contentString should startWith("""{"records":[{""")
    contentString should include(s"""tracks":[{"id":"${someUUID.toString.replaceAll("-", "")}"""")
  }

  it should "deliver track meta data even if there is no data in the backend" in {
    val trackServiceMock = mock[TrackService[TrackPoint]]

    val someUUID = UUID.randomUUID()
    when(trackServiceMock.allTracks)
      .thenReturn(Source(Nil))
    when(trackServiceMock.allDevices)
      .thenReturn(Seq())

    val controller = new FrontendController(trackServiceMock)
    val result = controller.trackMetaData()(FakeRequest())

    status(result) should be(OK)
    val contentString = contentAsString(result)
    contentString should startWith("""{"records":[""")
  }
}
