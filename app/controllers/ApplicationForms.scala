package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints.{max, min}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

import scala.util.matching.Regex

case class Login(name: String, email: String, userPassword: String)

case class Name(firstName: String, middleName: Option[String], lastName: String)

case class UserAddress(street: String, streetNo: Int, city: String)

case class UpdateUserForm(name: Name, age: Int, email: String, address: UserAddress, gender: String,
                          phoneNumber: Long, hobbies: List[Int] )

case class UpdatePassword(email: String, password: String, reCheckPassword: String)

case class AddAssignment(title: String, description: String)


case class SignUp(name: Name, age: Int, email: String, password: String, reCheckPassword: String,
                  address: UserAddress, gender: String, phoneNumber: Long, status: String, hobbies: List[String] )

class ApplicationForms {

  val userSignUpDataConstraints: Form[SignUp] = Form(
    mapping(
      "name" -> mapping(
        "firstName" -> text,
        "middleName" -> optional(text),
        "lastName" -> text
      )(Name.apply)(Name.unapply),
      "age" -> number.verifying(min(18), max(100)),
      "email" -> email,
      "password" -> nonEmptyText.verifying(passwordCheckConstraint),
      "reCheckPassword" -> nonEmptyText,
      "address" -> mapping(
        "street" -> text,
        "streetNo" -> number,
        "city" -> text
      )(UserAddress.apply)(UserAddress.unapply),
      "gender" -> text,
      "phoneNumber" -> longNumber,
      "status" -> text,
      "hobbies" -> list(nonEmptyText).verifying(nonEmptyList)
    )(SignUp.apply)(SignUp.unapply)
        .verifying("failed constraint",singUp => singUp.password == singUp.reCheckPassword))

  val updateUserForm: Form[UpdateUserForm] = Form(
    mapping(
      "name" -> mapping(
        "firstName" -> text,
        "middleName" -> optional(text),
        "lastName" -> text
      )(Name.apply)(Name.unapply),
      "age" -> number.verifying(min(18), max(100)),
      "email" -> email,
      "address" -> mapping(
        "street" -> text,
        "streetNo" -> number,
        "city" -> text
      )(UserAddress.apply)(UserAddress.unapply),
      "gender" -> text,
      "phoneNumber" -> longNumber,
      "hobbies" -> list(number).verifying(nonEmptyListOfID)
    )(UpdateUserForm.apply)(UpdateUserForm.unapply))

  val userLogInDataConstraints = Form(mapping(
    "name" -> nonEmptyText,
    "email" -> email,
    "userPassword" -> nonEmptyText)
  (Login.apply)(Login.unapply))

  val allNumbers: Regex = """\d*""".r
  val allLetters: Regex = """[A-Za-z]*""".r

  def nonEmptyListOfID: Constraint[List[Int]] = {
    Constraint("checkList.constraint")(
      {
        hobbies =>
          if(hobbies.isEmpty) Invalid(ValidationError("Select atleast one hobby!")) else Valid
      }
    )
  }

  def nonEmptyList: Constraint[List[String]] = {
    Constraint("checkList.constraint")(
      {
        hobbies =>
          if(hobbies.isEmpty) Invalid(ValidationError("Select atleast one hobby!")) else Valid
      }
    )
  }



  def passwordCheckConstraint: Constraint[String] = Constraint("passwordCheck")({
    PlainText =>
      val errors = PlainText match {
        case allNumbers()   => Seq(ValidationError("Password is all numbers"))
        case allLetters()   => Seq(ValidationError("Password is all letters"))
        case _              => Nil
      }
      if (errors.isEmpty) {
        Valid
      } else {
        Invalid(errors)
      }})

  val addAssignmentForm = Form(mapping(
    "title" -> nonEmptyText,
    "description" -> nonEmptyText
  )(AddAssignment.apply)(AddAssignment.unapply))

  val updatePasswordForm = Form(mapping(
    "email" -> email,
    "password" -> nonEmptyText.verifying(passwordCheckConstraint),
    "reCheckPassword" -> nonEmptyText.verifying(passwordCheckConstraint)
  )(UpdatePassword.apply)(UpdatePassword.unapply)
    .verifying(
      "The passwords did not match!",
      updatePassword => updatePassword.password == updatePassword.reCheckPassword
    ))
}
