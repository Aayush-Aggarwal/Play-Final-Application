package controllers

import javax.inject.Inject

import models._
import org.mindrot.jbcrypt.BCrypt
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UpdateController @Inject()(userRepository: UserRepository, hobbyRepository: HobbyRepository,
                                        userHobbyRepository: UserHobbyRepository, allForms: ApplicationForms,
                                        val messagesApi: MessagesApi) extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi
  lazy val hobbiesList: Future[List[Hobby]] = hobbyRepository.getHobbies

  def showProfile: Action[AnyContent] = Action.async { implicit request =>
    Logger.info("Show profile")
    val userIDString: Option[String] = request.session.get("userID")
    val isAdmin: Option[String] = request.session.get("isAdmin")

    userIDString match {

      case Some(userIDString) =>
        val userID: Int = userIDString.toInt
        Logger.info("Got the userID from the session! UserID = " + userID)
        userRepository.getUserByID(userID).flatMap {

          case Nil =>
            Logger.info("Did not receive any user with given UserID! Redirecting to welcome page!")
            Future.successful(Redirect(routes.ApplicationController.loginUser())
              .flashing("error" -> "Did not find any user with the given information. Please login again."))
          case userList: List[UserInfo] =>
            val user = userList.head
            userHobbyRepository.getUserHobby(userID).flatMap {
              case Nil =>
                Logger.info("Did not receive any hobbies for the user!")
                Future.successful(Redirect(routes.ApplicationController.loginUser())
                  .flashing("error" -> "Something went wrong since we did not find any hobbies with the given information. Please login again."))

              case hobbies: List[Int] =>
                Logger.info("Recieved list of hobbies")
                val updateUserFormValues = UpdateUserForm(Name(user.firstName, user.middleName, user.lastName),
                  user.age, user.email, UserAddress(user.street, user.streetNo, user.city)
                  , user.gender, user.phoneNumber , hobbies)
                hobbiesList.map(hobbies =>
                  Ok(views.html.profile(allForms.updateUserForm.fill(updateUserFormValues), hobbies, isAdmin))
                )
            }
        }
      case None => Future.successful(Ok(views.html.index()))
    }
  }

  def showProfilePost: Action[AnyContent] = Action.async { implicit request =>
    allForms.updateUserForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.error("Form was submitted with errors " + formWithErrors)
        hobbiesList.map(hobbies => BadRequest(views.html.profile(formWithErrors, hobbies, request.session.get("isAdmin"))))
      },
      updateUserData => {
        Logger.info("Form was successfully submitted")
        val optionOfID = request.session.get("userID")
        optionOfID match {
          case Some(userID) =>
            val updateUserForm = UpdateUserForm(Name(updateUserData.name.firstName,updateUserData.name.middleName
              ,updateUserData.name.lastName),  updateUserData.age,updateUserData.email,
              UserAddress(updateUserData.address.street,updateUserData.address.streetNo,updateUserData.address.city),
               updateUserData.gender, updateUserData.phoneNumber,
              updateUserData.hobbies)
            Logger.info("update profile")
            userRepository.updateUser(updateUserForm, userID.toInt).flatMap {
             /* case true =>
              Redirect(routes.UpdateController.showProfile).flashing("success" -> "User Profile successfully updated!")
             */ case true =>
                userHobbyRepository.deleteUserHobby(userID.toInt).flatMap {
                  case true =>
                      userHobbyRepository.addUserHobby(userID.toInt, updateUserData.hobbies.map(_.toInt)).map {
                      case true =>Logger.info("Updated")
                        Redirect(routes.UpdateController.showProfile).flashing("success" -> "User Profile successfully updated!")
                      case false => Redirect(routes.UpdateController.showProfile).flashing("error" -> "Something went wrong!")
                    }
                  case false => Future.successful(Redirect(routes.UpdateController.showProfile)
                    .flashing("error" -> "User Profile not updated due to errors!"))
                }
              case false => Future.successful(Redirect(routes.UpdateController.showProfile).flashing("error" -> "Email already exists!"))
            }
          case None => Future.successful(Ok(views.html.index()))
        }
      }
    )
  }

  def updatePassword: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.updatePassword(allForms.updatePasswordForm))
  }

  def updatePasswordPost: Action[AnyContent] = Action.async { implicit request =>
    allForms.updatePasswordForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.error("Form was submitted with errors " + formWithErrors)
        Future.successful(BadRequest(views.html.updatePassword(formWithErrors)))
      },
      updatePasswordData => {
        Logger.info("Form was successfully submitted!")
        val password = BCrypt.hashpw(updatePasswordData.password, BCrypt.gensalt())
        val updatePasswordForm = UpdatePassword(updatePasswordData.email, password, password)
        userRepository.updateUserByEmail(updatePasswordForm).map {
          case true =>
            Redirect(routes.DisplayView.logIn()).flashing("success" -> "Password successfully updated!")
          case false =>
            Redirect(routes.UpdateController.updatePassword).
              flashing("error" ->
                "The email entered was either invalid or is not registered with us.")

        }
      }
    )
  }

}
