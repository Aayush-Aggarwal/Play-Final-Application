package views

import controllers.{ApplicationForms, SignUp}
import models.Hobby
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.Flash

class SignUpTest extends PlaySpec with MockitoSugar {

  val hobbies: List[Hobby] = List(Hobby(1, "Programming"), Hobby(2, "Sports"))
  val allFormsObj = new ApplicationForms
  val signUpForm: Form[SignUp] = allFormsObj.userSignUpDataConstraints

  "signUp template" should {

    "render sign up page" in {

      val mockMessage = mock[Messages]
      val signUpPage = views.html.signup.render(signUpForm, hobbies, mockMessage,Flash(Map()))

      signUpPage.toString.contains("Signup Form!") mustEqual false
    }
  }
}
