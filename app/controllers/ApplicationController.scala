package controllers

import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import models.{UserInfo, UserRepository}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.Future

/**
  * Created by knoldus on 8/10/17.
  */
class ApplicationController @Inject()( val messagesApi: MessagesApi,
                                      val userRepo:UserRepository,val appForm:ApplicationForms) extends Controller with I18nSupport{


  def loginUser = Action {
    implicit request =>
      appForm.userLogInDataConstraints.bindFromRequest.fold(
        formWithErrors => {
          Logger.info("Error Detected" + formWithErrors)
          BadRequest(views.html.User(formWithErrors))
        },
        login => {
          Logger.info("Login Successfully" + login)
          Redirect(routes.DisplayView.successLoginDisplay())
        })
  }

  def signUpUser: Action[AnyContent] = Action.async{
    implicit request =>
      appForm.userSignUpDataConstraints.bindFromRequest.fold(
        formWithErrors => {
          Logger.info("Error Detected" + formWithErrors)
          Future.successful(BadRequest(views.html.signup(formWithErrors)))
        },
        login => {
          Logger.info("Login Successfully" + login)
          val userData = UserInfo(0,login.name.firstName,login.name.middleName,login.name.lastName,
            login.age,login.email,login.password,login.address.street,login.address.streetNo,
            login.address.city,login.gender,login.phoneNumber,login.status)
          userRepo.addNewUser(userData).map{
            case true => Redirect(routes.DisplayView.successSignUpDisplay())

            case false => Redirect(routes.DisplayView.error())
          }

        })
  }

}