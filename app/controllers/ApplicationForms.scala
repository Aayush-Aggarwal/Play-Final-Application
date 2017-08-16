package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints.{max, min}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

import scala.util.matching.Regex

case class Login(name: String, email: String, userPassword: String)

case class Name(firstName: String, middleName: Option[String], lastName: String)

case class UserAddress(street: String, streetNo: Int, city: String)

case class UpdateUserForm(name: Name, phoneNo: Long,
                          gender: String, age: Int, hobbies: List[Int])

case class UpdatePassword(email: String, password: String, reCheckPassword: String)

case class AddAssignment(title: String, description: String)


case class SignUp(name: Name, age: Int, email: String, password: String, reCheckPassword: String,
                  address: UserAddress, gender: String, phoneNumber: Long, status: String)

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
      "status" -> text
    )(SignUp.apply)(SignUp.unapply)
      verifying("failed constraint",singUp => singUp.password == singUp.reCheckPassword))

  val userLogInDataConstraints = Form(mapping(
    "name" -> nonEmptyText,
    "email" -> email,
    "userPassword" -> nonEmptyText)
  (Login.apply)(Login.unapply))

  val allNumbers: Regex = """\d*""".r
  val allLetters: Regex = """[A-Za-z]*""".r

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

}

/*  .verifying("Failed form constraints!", fields => fields match {
      case login => validateData(login.name, login.age, login.email, login.userPassword).isDefined
    })
    def validateData(name: String, age: Int, email: String, userPassword: String):Option[Login] = {
      name match {
        case "Ayush" if name.isEmpty => Some(Login(name, age, email, userPassword))
        case "admin" => Some(Login(name, age, email, userPassword))
        case _ => None
      }
    }*/