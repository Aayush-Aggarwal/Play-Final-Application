package views

import controllers.{ApplicationForms, Login}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.Flash

class LoginTest extends PlaySpec with MockitoSugar {

  val allFormsObj = new ApplicationForms
  val loginForm: Form[Login] = allFormsObj.userLogInDataConstraints

  "Login template" should {

    "render the login page" in {

      val mockMessages = mock[Messages]
      val loginPage = views.html.User.render(loginForm, mockMessages, Flash(Map()))

      loginPage.toString.contains("Login Form!") mustEqual false
    }
  }
}
