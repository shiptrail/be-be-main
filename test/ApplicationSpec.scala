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

    "send 404 on a bad request" in  {
      route(app, FakeRequest(GET, "/boum")).map(status(_)) mustBe Some(NOT_FOUND)
    }

  }

  "HomeController" should {

    "render the index page" in {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Your new application is ready.")
    }

  }

  "BackendController V1" should {

    val validClientUpdate =
      """
        [
          {
            "id": 1,
            "lat": 0.0,
            "lng": 0.0,
            "ele": 0.0,
            "heading": 360,
            "timestamp": 100
          },
          {
            "id": 2,
            "lat": 1.0,
            "lng": 1.0,
            "ele": 10.0,
            "heading": 360,
            "timestamp": 100
          }
        ]
      """

    val invalidClientUpdate =
      """
        [
          {
            "id": 1,
            "lat": 0.0,
            "lng": 0.0,
            "ele": 0.0,
            "heading": 360,
            "timestamp": 100
          },
          {
            "id": 2,
            "lat": -1000.0,
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
        controllers.routes.BackendController.send().url,
        FakeHeaders(
          Seq("Content-type"->"application/json")
        ),
        validClientUpdate
      )

      val Some(result) = route(app, validRequest)

      status(result) mustBe OK
    }

    "reject invalid json" in {
      val validRequest = FakeRequest(
        Helpers.POST,
        controllers.routes.BackendController.send().url,
        FakeHeaders(
          Seq("Content-type"->"application/json")
        ),
        invalidClientUpdate
      )

      val Some(result) = route(app, validRequest)

      status(result) mustBe BAD_REQUEST
    }
  }


  "BackendController V2" should {

    val validClientUpdate =
      """
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
        controllers.routes.BackendController.send2(UUID.randomUUID()).url,
        FakeHeaders(
          Seq("Content-type"->"application/json")
        ),
        validClientUpdate
      )

      val Some(result) = route(app, validRequest)

      status(result) mustBe OK
    }

  }
}
