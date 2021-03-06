package controllers

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test._
import services.TrackService

import scala.concurrent.Future

class BackendControllerTest extends FlatSpec with Matchers with MockitoSugar {

  implicit val sys = ActorSystem()
  implicit val mat = ActorMaterializer()

  "Backend Controller" should "pass data from the track service" in {
    val trackServiceMock = mock[TrackService[Int]]

    val someUUID = UUID.randomUUID()

    val controller = new AbstractBackendController[Int](trackServiceMock)

    val singleTrackPoint = FakeRequest(
        Helpers.POST,
        "",
        FakeHeaders(),
        List(1)
    )

    val result: Future[Result] = controller.send(someUUID)()(singleTrackPoint)

    status(result) should be(NO_CONTENT)
    contentAsString(result) should be(empty)

    verify(trackServiceMock, times(1)).consume(any(), any())
  }

  it should "pass all given track points to the track service" in {
    val numberOfPoints: Int = 3

    val someUUID = UUID.randomUUID()

    val trackServiceMock = mock[TrackService[Int]]

    val controller = new AbstractBackendController[Int](trackServiceMock)
    val multiplePoints = FakeRequest(
        Helpers.POST,
        "",
        FakeHeaders(),
        List.fill(numberOfPoints)(1)
    )

    val result: Future[Result] = controller.send(someUUID)()(multiplePoints)

    status(result) should be(NO_CONTENT)
    contentAsString(result) should be(empty)

    verify(trackServiceMock, times(numberOfPoints)).consume(any(), any())
  }
}
