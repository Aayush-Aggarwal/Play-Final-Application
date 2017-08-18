package controllers

import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import models._
import org.mindrot.jbcrypt.BCrypt
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}

import scala.concurrent.Future

/**
  * Created by knoldus on 8/10/17.
  */
class ApplicationController @Inject()(val messagesApi: MessagesApi, val userHobbyRepository: UserHobbyRepository,
                                      val hobbyRepository: HobbyRepository,
                                      val userRepo: UserRepository, val appForm: ApplicationForms) extends Controller with I18nSupport {


  def loginUser: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    appForm.userLogInDataConstraints.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("Error Detected" + formWithErrors)
        Future.successful(BadRequest(views.html.User(formWithErrors)))
      },
      login => {
        userRepo.checkIfUserExists(login.email, login.userPassword).flatMap {
          case true => {
            Logger.info("User exists!")
            userRepo.getUserInfoForSession(login.email).map {
              case Nil => Redirect(routes.DisplayView.logIn()).flashing("error" -> "Something went wrong!")
              case listOfUserInfo: List[(Int, Boolean, Boolean)] =>
                if (!listOfUserInfo.head._3) {
                  Redirect(routes.DisplayView.logIn())
                    .flashing("error" -> "Sorry! You are not enabled and thus cannot login. Please contact the site administrator.")
                }
                else if (!listOfUserInfo.head._2) {
                  Redirect(routes.UpdateController.showProfile).withSession("userID" -> s"${listOfUserInfo.head._1}", "isAdmin" -> "false")
                }
                else {
                  Redirect(routes.UpdateController.showProfile).withSession("userID" -> s"${listOfUserInfo.head._1}", "isAdmin" -> "true")
                }
            }
          }
          case false => Future.successful(Redirect(routes.DisplayView.logIn)
            .flashing("error" -> "Username and password combination did not match!", "email" -> s"${login.email}"))
        }

      })
  }

  lazy val hobbiesList: Future[List[Hobby]] = hobbyRepository.getHobbies

  def signUpUser: Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      appForm.userSignUpDataConstraints.bindFromRequest.fold(
        formWithErrors => {
          Logger.info("Error Detected" + formWithErrors)

          hobbiesList.map(hobbies => BadRequest(views.html.signup(formWithErrors, hobbies)))
        },
        login => {
          userRepo.checkEmail(login.email).flatMap {
            case true =>
              Logger.info("userData = " + login)
              val hashPassword = BCrypt.hashpw(login.password, BCrypt.gensalt())

              val userData = UserInfo(0, login.name.firstName, login.name.middleName, login.name.lastName,
                login.age, login.email, hashPassword, login.address.street, login.address.streetNo,
                login.address.city, login.gender, login.phoneNumber, login.status, false, true)
              userRepo.addNewUser(userData).flatMap {
                case true =>
              //    Logger.info("added new user")
                  userRepo.getUserID(userData.email).flatMap {
                    case Nil => Future.successful(Redirect(routes.ApplicationController.signUpUser())
                      .flashing("error" -> "Something went wrong and we weren't able to retrieve your account's ID."))
                    case id: List[Int] =>
                      userHobbyRepository.addUserHobby(id.head, login.hobbies.map(_.toInt)).map {
                        case true => Redirect(routes.UpdateController.showProfile).withSession("userID" -> s"${id.head}")
                        case false => Redirect(routes.DisplayView.signUp())
                          .flashing("error" -> "Something went wrong and we weren't able to store your selected hobbies.")
                      }
                  }
                case false => Future.successful(Redirect(routes.DisplayView.signUp())
                  .flashing("error" -> "Something went wrong and we weren't able to store your information. Please sign up again."))
              }
            case false => Future.successful(Redirect(routes.DisplayView.signUp())
              .flashing("error" -> "Entered email already exists. If you're an existing member then please sign in!"))
          }
        }/*
          Logger.info("Login Successfully" + login)
          val userData = UserInfo(0, login.name.firstName, login.name.middleName, login.name.lastName,
            login.age, login.email, hashPassword, login.address.street, login.address.streetNo,
            login.address.city, login.gender, login.phoneNumber, login.status, false, true)
          userRepo.addNewUser(userData).map {
            case true => Redirect(routes.DisplayView.signUp())

            case false => Redirect(routes.DisplayView.error())
          }

        }*/)
  }


  def logout: Action[AnyContent] = Action {
    Redirect(routes.HomeController.index()).withNewSession
  }

}
