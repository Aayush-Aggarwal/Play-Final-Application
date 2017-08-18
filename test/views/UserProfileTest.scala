package views

import controllers.{ApplicationForms, UpdateUserForm}
import models.Hobby
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.Flash

class UserProfileTest extends PlaySpec with MockitoSugar {

  val allFormsObj = new ApplicationForms
  val updateUserForm: Form[UpdateUserForm] = allFormsObj.updateUserForm
  val hobbies: List[Hobby] = List(Hobby(1, "Programming"), Hobby(2, "Sports"))

  "User profile template" should {

    "render user profile page" in {

      val mockMessages = mock[Messages]
      val userProfilePage = views.html.profile.render(updateUserForm, hobbies, Some("false"), mockMessages)

      userProfilePage.toString.contains("SignUp information") mustEqual false
    }
  }
}
