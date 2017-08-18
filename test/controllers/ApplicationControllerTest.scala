package controllers

import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import models.{_}
import org.h2.engine.User
import org.mockito._
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.{Configuration, Environment}
import play.api.data.Form
import play.api.i18n.{DefaultLangs, DefaultMessagesApi, MessagesApi}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class ApplicationControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {


  implicit lazy val materializer: Materializer = app.materializer
  val mockUserRepository: UserRepository = mock[UserRepository]
  val allFormsObj: ApplicationForms = new ApplicationForms
  val name: Name = Name("Ayush", None, "Aggarwal")
  val address: UserAddress = UserAddress("abc", 2, "delhi")
  val hobbiesNameList: List[String] = List("Programming", "Sports")
  val hobbies: List[Hobby] = List(Hobby(1, "Programming"), Hobby(2, "Sports"))
  val user: SignUp = SignUp(name, 21, "ayush@knoldus.com", "ayush123", "ayush123", address, "male", 9999899877L, "single", hobbiesNameList)
  val userForm: Form[SignUp] = allFormsObj.userSignUpDataConstraints
  val mockAllForms: ApplicationForms = mock[ApplicationForms]
  val allForms: ApplicationForms = new ApplicationForms
  val messages: MessagesApi = mock[MessagesApi]
  val mockHobbyRepository: HobbyRepository = mock[HobbyRepository]
  val mockUserHobbyRepository: UserHobbyRepository = mock[UserHobbyRepository]
  val hobbyList: Future[List[String]] = Future.successful(List("Programming", "Reading", "Sports", "Writing", "Swimming"))
  val config: Configuration = Configuration(ConfigFactory.load("application.conf"))
  val defaultMessages: DefaultMessagesApi = new DefaultMessagesApi(Environment.simple(), config, new DefaultLangs(config))
  val signUpController: ApplicationController = new ApplicationController(defaultMessages,
    mockUserHobbyRepository, mockHobbyRepository, mockUserRepository, mockAllForms)

  "SignUpController" should {

    "show the sign up page" in {

      when(mockHobbyRepository.getHobbies).thenReturn(Future(hobbies))

      when(mockAllForms.userSignUpDataConstraints).thenReturn(userForm)

      val result = call(signUpController.signUpUser, FakeRequest(GET, "/signup").withFormUrlEncodedBody(
        "name.firstName" -> "Ayush", "name.middleName" -> "", "name.lastName" -> "Aggarwal", "age" -> "21"
        , "email" -> "ayush@knoldus.com", "password" -> "ayushh123", "reCheckPassword" -> "ayushh123", "address.street" -> "gali",
        "address.streetNo" -> "2", "city" -> "delhi", "gender" -> "male", "mobileNo" -> "9999899877",
        "status" -> "single", "hobbies[0]" -> "1", "hobbies[1]" -> "3"))

      status(result) mustEqual 400
    }

    "not be able to retrieve user ID from database" in {

      when(mockUserRepository.checkEmail("ayush@knoldus.com")).thenReturn(Future(true))

      when(mockUserRepository.addNewUser(any(classOf[UserInfo]))).thenReturn(Future(true))

      // when(mockHobbyRepository.getHobbyIDs(hobbies)).thenReturn(Future(List(List(1,3))))

      when(mockUserRepository.getUserID("ayush@knoldus.com")).thenReturn(Future(Nil))

      when(mockAllForms.userSignUpDataConstraints).thenReturn(userForm)

      val result = call(signUpController.signUpUser, FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Ayush", "name.middleName" -> "", "name.lastName" -> "Aggarwal", "age" -> "21"
        , "email" -> "ayush@knoldus.com", "password" -> "ayushh123", "reCheckPassword" -> "ayushh123", "address.street" -> "gali",
        "address.streetNo" -> "2", "city" -> "delhi", "gender" -> "male", "mobileNo" -> "9999899877",
        "status" -> "single", "hobbies[0]" -> "1", "hobbies[1]" -> "3")
      )

      redirectLocation(result) mustBe Some("/signup")
    }

    "not be able to add user hobby in database" in {

      when(mockUserRepository.checkEmail("ayush@knoldus.com")).thenReturn(Future(true))

      when(mockUserRepository.addNewUser(any(classOf[UserInfo]))).thenReturn(Future(true))

      // when(mockHobbyRepository.getHobbyIDs(hobbies)).thenReturn(Future(List(List(1,3))))

      when(mockUserRepository.getUserID("ayush@knoldus.com")).thenReturn(Future(List(1)))

      when(mockUserHobbyRepository.addUserHobby(1, List(1, 3))).thenReturn(Future(false))

      when(mockAllForms.userSignUpDataConstraints).thenReturn(userForm)

      val result = call(signUpController.signUpUser, FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Ayush", "name.middleName" -> "", "name.lastName" -> "Aggarwal", "age" -> "21"
        , "email" -> "ayush@knoldus.com", "password" -> "ayushh123", "reCheckPassword" -> "ayushh123", "address.street" -> "gali",
        "address.streetNo" -> "2", "city" -> "delhi", "gender" -> "male", "mobileNo" -> "9999899877",
        "status" -> "single", "hobbies[0]" -> "1", "hobbies[1]" -> "3")
      )

      redirectLocation(result) mustBe Some("/signup")
    }

    "be able to add user hobby in database" in {

      when(mockUserRepository.checkEmail("ayush@knoldus.com")).thenReturn(Future(true))

      when(mockUserRepository.addNewUser(any(classOf[UserInfo]))).thenReturn(Future(true))

      //when(mockHobbyRepository.getHobbyIDs(hobbies)).thenReturn(Future(List(List(1,3))))

      when(mockUserRepository.getUserID("ayush@knoldus.com")).thenReturn(Future(List(1)))

      when(mockUserHobbyRepository.addUserHobby(1, List(1, 3))).thenReturn(Future(true))

      when(mockAllForms.userSignUpDataConstraints).thenReturn(userForm)

      val result = call(signUpController.signUpUser, FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Ayush", "name.middleName" -> "", "name.lastName" -> "Aggarwal", "age" -> "21"
        , "email" -> "ayush@knoldus.com", "password" -> "ayushh123", "reCheckPassword" -> "ayushh123", "address.street" -> "gali",
        "address.streetNo" -> "2", "city" -> "delhi", "gender" -> "male", "mobileNo" -> "9999899877",
        "status" -> "single", "hobbies[0]" -> "1", "hobbies[1]" -> "3")
      )

      redirectLocation(result) mustBe Some("/showprofile")
    }

    "not be able to add the user in database" in {

      when(mockUserRepository.checkEmail("ayush@knoldus.com")).thenReturn(Future(true))

      when(mockUserRepository.addNewUser(any(classOf[UserInfo]))).thenReturn(Future(false))

      when(mockAllForms.userSignUpDataConstraints).thenReturn(userForm)

      val result = call(signUpController.signUpUser, FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Ayush", "name.middleName" -> "", "name.lastName" -> "Aggarwal", "age" -> "21"
        , "email" -> "ayush@knoldus.com", "password" -> "ayushh123", "reCheckPassword" -> "ayushh123", "address.street" -> "gali",
        "address.streetNo" -> "2", "city" -> "delhi", "gender" -> "male", "mobileNo" -> "9999899877",
        "status" -> "single", "hobbies[0]" -> "1", "hobbies[1]" -> "3")
      )

      redirectLocation(result) mustBe Some("/signup")
    }

    "not be able to find the email in database" in {

      when(mockUserRepository.checkEmail("ayush@knoldus.com")).thenReturn(Future(false))

      when(mockAllForms.userSignUpDataConstraints).thenReturn(userForm)

      val result = call(signUpController.signUpUser, FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Ayush", "name.middleName" -> "", "name.lastName" -> "Aggarwal", "age" -> "21"
        , "email" -> "ayush@knoldus.com", "password" -> "ayushh123", "reCheckPassword" -> "ayushh123", "address.street" -> "gali",
        "address.streetNo" -> "2", "city" -> "delhi", "gender" -> "male", "mobileNo" -> "9999899877",
        "status" -> "single", "hobbies[0]" -> "1", "hobbies[1]" -> "3")
      )

      redirectLocation(result) mustBe Some("/signup")

}
    "receive form with errors" in {

      when(mockAllForms.userSignUpDataConstraints).thenReturn(userForm)

      when(mockHobbyRepository.getHobbies).thenReturn(Future(hobbies))

      val result = call(signUpController.signUpUser, FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Ayush", "name.middleName" -> "", "name.lastName" -> "Aggarwal", "age" -> "21"
        , "email" -> "ayush@knoldus.com", "password" -> "ayushh123", "reCheckPassword" -> "ayushh123", "address.street" -> "gali",
        "address.streetNo" -> "2", "city" -> "delhi", "gender" -> "male", "mobileNo" -> "9999899877",
        "status" -> "single", "hobbies[0]" -> "1", "hobbies[1]" -> "3")
      )

      status(result) mustEqual BAD_REQUEST
    }
  }
}

