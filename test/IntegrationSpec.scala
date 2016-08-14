import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

/**
  * add your integration spec here.
  * An integration test will fire up a whole play application in a real (or headless) browser
  */
class IntegrationSpec
    extends PlaySpec
    with OneServerPerTest
    with OneBrowserPerTest
    with ChromeFactory
  //  with HtmlUnitFactory
    with ScalaFutures {

  "Application" should {

    "pass submitted points to browser" in {

      go to (s"http://localhost:$port/test")

      pageSource must not include "19.999999"

      val client = app.injector.instanceOf[WSClient]
      client
        .url(s"http://localhost:$port/v2/1589de3c-e60e-4107-ab1b-dde474a2b5fd/send")
        .post(
            Json.arr(
                Json.obj(
                    "lat" -> 19.9999999999,
                    "lng" -> 0,
                    "ele" -> 0,
                    "heading" -> 0,
                    "timestamp" -> 10000
                )
            )
        )
        .futureValue
        .body must be(empty)

      pageSource must include("19.999999")
    }
  }
}
