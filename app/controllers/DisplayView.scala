package controllers

import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import models.{Hobby, HobbyRepository}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}

import scala.concurrent.Future

/**
  * Created by knoldus on 8/10/17.
  */
class DisplayView @Inject()(implicit val messagesApi: MessagesApi,
                            val hobbyRepository: HobbyRepository) extends Controller with I18nSupport {

  val appForm = new ApplicationForms
  lazy val hobbiesList: Future[List[Hobby]] = hobbyRepository.getHobbies

  def successLoginDisplay(): Action[AnyContent] = Action {
    implicit request => Ok(views.html.User(appForm.userLogInDataConstraints))
  }


  def successSignUpDisplay(): Action[AnyContent] = Action.async {implicit request: Request[AnyContent]=>
    hobbiesList.map(hobbies => Ok(views.html.signup(appForm.userSignUpDataConstraints, hobbies)))
    }

  def logIn: Action[AnyContent] = Action {
    implicit request => Ok(views.html.User(appForm.userLogInDataConstraints))
  }

  def signUp: Action[AnyContent] = Action.async{implicit request: Request[AnyContent]=>

    hobbiesList.map(hobbies => Ok(views.html.signup(appForm.userSignUpDataConstraints, hobbies)))

  }

  def error(): Action[AnyContent] = Action {
    implicit request => Ok("Error detected")
  }
}
