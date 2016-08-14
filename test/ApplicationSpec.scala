import java.util.UUID

import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test._

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  * For more information, consult the wiki.
  */
class ApplicationSpec extends PlaySpec with OneAppPerTest {

  "Routes" should {

    "send 404 on a bad request" in {
      route(app, FakeRequest(GET, "/boum")).map(status) mustBe Some(NOT_FOUND)
    }
  }

  "BackendController V2" should {

    val validClientUpdate = """
        [
          {
            "lat": 0.0,
            "lng": 0.0,
            "ele": 0.0,
            "heading": 360,
            "timestamp": 100
          },
          {
            "lat": 1.0,
            "lng": 1.0,
            "ele": 10.0,
            "heading": 360,
            "timestamp": 100
          }
        ]
      """

    "accept valid json" in {
      val validRequest = FakeRequest(
          Helpers.POST,
          controllers.routes.BackendController.send(UUID.randomUUID()).url,
          FakeHeaders(
              Seq("Content-type" -> "application/json")
          ),
          validClientUpdate
      )

      val Some(result) = route(app, validRequest)

      status(result) mustBe OK
    }
  }
}
