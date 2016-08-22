package controllers


import org.scalatest.{FlatSpec, Matchers}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}


class LandingPageControllerTest extends FlatSpec with Matchers {

  "LandingPageController.showFrontend" should "redirect to /fe" in {

    val controller = new LandingPageController()

    val validRequest = FakeRequest(
      Helpers.GET,
      controllers.routes.LandingPageController.showFrontend().url
    )

    val result = controller.showFrontend()(validRequest)

    status(result) should be(SEE_OTHER)
    redirectLocation(result) should be(Some("/fe"))
  }

  "LandingPageController.showTestPage" should "redirect to /test" in {

    val controller = new LandingPageController()

    val result = controller.showTestPage()(FakeRequest())

    status(result) should be(SEE_OTHER)
    redirectLocation(result) should be(Some("/test"))
  }
}
