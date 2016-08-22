package controllers

import play.api.mvc.{Action, Controller}


class LandingPageController extends Controller {

  def showFrontend() = Action { request =>
    Redirect("/fe")
  }

  def showTestPage() = Action { request =>
    Redirect("/test")
  }
}
