package controllers

import play.api.mvc.{Action, Controller}

class LandingPageController extends Controller {

  def showProdFrontend() = Action { request =>
    Redirect("/fe")
  }

  def showDevFrontend() = Action { request =>
    Redirect("http://localhost:3000/#/")
  }
}
