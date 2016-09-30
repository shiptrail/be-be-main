import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

/**
  * add your integration spec here.
  * An integration test will fire up a whole play application in a real chrome (-ium) browser
  */
class IntegrationTest
    extends PlaySpec
    with OneServerPerTest
    with OneBrowserPerTest
    with ChromeFactory
    with ScalaFutures {

  "Application" should {

    "pass submitted points to browser" in {
      val deviceId: String = "1589de3c-e60e-4107-ab1b-dde474a2b5fd"
      val deviceLatitude = 19.9999999999

      go to s"http://localhost:$port/test"

      pageSource must not include deviceLatitude.toString
      pageSource must not include deviceId

      val client = app.injector.instanceOf[WSClient]

      client
        .url(s"http://localhost:$port/v2/$deviceId/send")
        .post(
            Json.arr(
                Json.obj(
                    "lat" -> deviceLatitude,
                    "lng" -> 0,
                    "ele" -> 0,
                    "heading" -> 0,
                    "timestamp" -> 10000
                )
            )
        )

      eventually {
        pageSource must include(deviceLatitude.toString)
        pageSource must include("1589de3ce60e4107ab1bdde474a2b5fd")
      }

    }
  }
}
