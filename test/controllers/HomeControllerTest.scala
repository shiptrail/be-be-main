package controllers

import org.scalatestplus.play.PlaySpec
import play.api.mvc.{Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class HomeControllerTest extends PlaySpec with Results {

  "HomeController" should {
    "should accept valid requestg" in {
      val controller = new HomeController()
      val result: Future[Result] = controller.index()(FakeRequest())
      val bodyText: String = contentAsString(result)

      status(result) mustEqual OK
      bodyText must include("<script")
    }
  }
}
