package controllers

import akka.stream.Materializer
import models.{UserInfo, UserRepository}
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class ApplicationControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite  {
  implicit lazy val materializer: Materializer = app.materializer

  val messageMock: MessagesApi = mock[MessagesApi]
  val repo: UserRepository = mock[UserRepository]
  val form: ApplicationForms = mock[ApplicationForms]
  val applicationController = new ApplicationController(messageMock,repo,form)

  "Application-Controller" should{
    "be able to create user account" in{
      val user = SignUp(Name("ayush",None,"Aggarwal"),21,"ayush@konoldus.com","dontknow",
        "dontKnow",UserAddress("41",4,"Noida"),"male",7838467221l,"single")

      val forms = new ApplicationForms().userSignUpDataConstraints.fill(user)

      when(form.userSignUpDataConstraints).thenReturn(forms)
      when(repo.findByEmail("ayush@knoldus.com")).thenReturn(Future(None))
      when(repo.addNewUser(UserInfo(0,"ayush",None,"Aggarwal",21,"ayush@konoldus.com","dontknow"
        ,"41",4,"Noida","male",7838467221l,"single"))).thenReturn(Future(true))

      val result = applicationController.signUpUser.apply(FakeRequest(POST,"/signup")
          .withFormUrlEncodedBody(
            "firstName" -> "ayush",
            "middleName" -> "",
            "lastName" -> "Aggarwal",
            "age" -> "21",
            "email" -> "ayush@konoldus.com",
            "password" -> "dontknow",
            "reCheckPassword" -> "dontknow",
            "street" -> "41",
            "streetNo" -> "4",
            "city" -> "Noida",
            "gender" -> "male",
            "phoneNumber" -> "7838467221l",
            "status" -> "single"
          ))

      redirectLocation(result) mustBe Some("/success")

    }
  }
}
