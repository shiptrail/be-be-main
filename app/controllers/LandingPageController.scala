package controllers

import play.api.mvc.{Action, Controller}


class LandingPageController extends Controller {

  def showFrontend() = Action { request =>
    Redirect("http://localhost:3000/#/")
  }

}
