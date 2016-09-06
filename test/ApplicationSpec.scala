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

  "BackendController V2a" should {

    val validClientUpdate =
      """
        [
          {
            "lat": 0.0,
            "lng": 0.0,
            "ele": 0.0,
            "heading": 360,
            "timestamp": 100,
            "orientation": [
              {
                "azimuth": 0.0,
                "pitch": 0.0,
                "roll": 0.0,
                "toffset": 0
              }
            ],
            "annotation": [
              {
                "type": "START_JIBE",
                "toffset": 0
              }
            ]
          },
          {
            "lat": 1.0,
            "lng": 1.0,
            "ele": 10.0,
            "heading": 360,
            "timestamp": 200,
            "orientation": [
              {
                "azimuth": 90.0,
                "pitch": 90.0,
                "roll": 90.0,
                "toffset": 0
              }
            ],
            "annotation": [
              {
                "type": "MID_JIBE",
                "toffset": 0
              }
            ]
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

      status(result) mustBe NO_CONTENT
    }
  }

  it should {

    val invalidClientUpdate =
      """
        [
          {
            "lat": 0.0,
            "lng": 0.0,
            "ele": 0.0,
            "heading": 360,
            "timestamp": 100,
            "orientation": [
              {
                "azimuth": 0.0,
                "pitch": 0.0,
                "roll": 0.0,
                "toffset": 0
              }
            ],
            "annotation": [
              {
                "type": "UNKNOWN_FOOBAR_EVENT",
                "toffset": 0
              }
            ]
          }
        ]
      """

    "accept requests with unknown annotations" in {
      val validRequest = FakeRequest(
        Helpers.POST,
        controllers.routes.BackendController.send(UUID.randomUUID()).url,
        FakeHeaders(
          Seq("Content-type" -> "application/json")
        ),
        invalidClientUpdate
      )

      val Some(result) = route(app, validRequest)

      status(result) mustBe NO_CONTENT
    }
  }
}
