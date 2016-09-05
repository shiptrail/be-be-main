package controllers

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import play.api.test.Helpers._
import play.api.test._
import services.TrackService

class FrontendControllerTest extends FlatSpec with Matchers with MockitoSugar {

  implicit val sys = ActorSystem()
  implicit val mat = ActorMaterializer()

  "Front Controller" should "pass data from the track service" in {
    val trackServiceMock = mock[TrackService[Int]]

    val someUUID = UUID.randomUUID()

    when(trackServiceMock.allTracks).thenReturn(Source(someUUID -> 1 :: Nil))
    val controller = new AbstractFrontendController[Int](trackServiceMock)

    val validRequest = FakeRequest(
        Helpers.POST,
        controllers.routes.FrontendController.allTracks().url
    )

    val result = controller.allTracks()(validRequest)

    status(result) should be(OK)
    contentAsString(result) should include(someUUID.toString)
  }
}
