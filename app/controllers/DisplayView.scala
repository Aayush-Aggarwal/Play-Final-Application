package controllers

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

/**
  * Created by knoldus on 8/10/17.
  */
class DisplayView @Inject()(implicit val messagesApi: MessagesApi) extends Controller with I18nSupport {

  val appForm =new ApplicationForms

  def successLoginDisplay() = Action {
    implicit request => Ok(views.html.displaylogindata())
  }


  def successSignUpDisplay() = Action {
    implicit request => Ok(views.html.displaysignupdata())
  }

  def logIn =Action {
    implicit request => Ok(views.html.User(appForm.userLogInDataConstraints))
  }

  def signUp =Action {
    implicit request => Ok(views.html.signup(appForm.userSignUpDataConstraints))
  }

  def error() = Action{
    implicit  request =>Ok("Error detected")
  }
}
