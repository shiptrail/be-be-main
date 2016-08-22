package controllers


import org.scalatest.{FlatSpec, Matchers}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}


class LandingPageControllerTest extends FlatSpec with Matchers {

  "LandingPageController.showProdFrontend" should "redirect to /fe" in {

    val controller = new LandingPageController()

    val result = controller.showProdFrontend()(FakeRequest())

    status(result) should be(SEE_OTHER)
    redirectLocation(result) should be(Some("/fe"))
  }

  "LandingPageController.showDevPage" should "redirect to http://localhost:3000/#/" in {

    val controller = new LandingPageController()

    val result = controller.showDevFrontend()(FakeRequest())

    status(result) should be(SEE_OTHER)
    redirectLocation(result) should be(Some("http://localhost:3000/#/"))
  }
}
