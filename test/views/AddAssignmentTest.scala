package views

import controllers.{AddAssignment, ApplicationForms}
import models.Assignment
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.Form
import play.api.i18n.Messages

class AddAssignmentTest extends PlaySpec with MockitoSugar {

  val allFormsObj = new ApplicationForms
  val addAssignmentForm: Form[AddAssignment] = allFormsObj.addAssignmentForm
  val assignment = List(Assignment(1, "title", "description"))

  "addAssignment template" should {

    "render addAssignment page" in {

      val mockMesssages = mock[Messages]
      val addAssignmentPage = views.html.addAssignment.render(addAssignmentForm, assignment, Some("true"), mockMesssages)

      addAssignmentPage.toString.contains("Add Assignments") mustEqual true
    }
  }
}
