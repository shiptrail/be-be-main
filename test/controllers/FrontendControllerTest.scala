package controllers

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import models.TrackPoint
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test._
import services.TrackService
import org.mockito.Mockito._

class FrontendControllerTest
    extends PlaySpec
    with OneAppPerTest
    with MockitoSugar {

  implicit val sys = ActorSystem()
  implicit val mat = ActorMaterializer()

  "FrontendController" should {

    "pass data from the track service" in {
      val trackServiceMock = mock[TrackService[TrackPoint]]

      val someUUID = UUID.randomUUID()

      when(trackServiceMock.allTracks)
        .thenReturn(Source(someUUID -> TrackPoint(10, 10, 10, 10, 10) :: Nil))
      val controller = new FrontendController(trackServiceMock)

      val validRequest = FakeRequest(
          Helpers.POST,
          controllers.routes.FrontendController.allTracks().url
      )

      val result = controller.allTracks()(validRequest)

      status(result) mustBe OK
      contentAsString(result) must include(someUUID.toString)
    }
  }
}
