package models

import scala.concurrent.ExecutionContext.Implicits.global
import com.google.inject.Inject
import controllers.{UpdatePassword, UpdateUserForm}
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import play.api.Logger

import scala.concurrent.Future

case class UserInfo(id: Int, firstName: String, middleName: Option[String], lastName: String,
                    age: Int, email: String, password: String, street: String, streetNo: Int, city: String,
                    gender: String, phoneNumber: Long, status: String, isAdmin:Boolean,isEnabled:Boolean)

class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserTable {

  import driver.api._

  def addNewUser(user: UserInfo): Future[Boolean] = {
    db.run(userQuery += user).map(_ > 0)
  }

  def checkEmail(email: String): Future[Boolean] = {
    Logger.info("Checking if email exists in Database")
    val emailList = db.run(userQuery.filter(_.email === email).to[List].result)
    emailList.map { email =>
      if (email.isEmpty) true else false
    }
  }

  def checkIfUserExists(email: String, password: String): Future[Boolean] = {
    Logger.info("Checking if user exists in Database")
    val userList = db.run(userQuery.filter(_.email === email).to[List].result)
    userList.map { user =>
      if (user.isEmpty) {
        false
      }
      else if (!BCrypt.checkpw(password, user.head.password)) {
        false
      }
      else {
        true
      }
    }
  }

  /*def getEmail(username: String): Future[String] = {
    Logger.info("Sending data for maintaining session for user")
    val userList: Future[List[User]] = db.run(userQuery.filter(_.username === username).to[List].result)
    userList.map(user => user.head.email)
  }*/

  def getUser(email: String): Future[List[UserInfo]] = {
    Logger.info("Retrieving user from database from email stored in session")
    db.run(userQuery.filter(_.email === email).to[List].result)
  }

  def getUserByID(userID: Int): Future[List[UserInfo]] = {
    Logger.info("Retrieving user from database from ID stored in session")
    db.run(userQuery.filter(_.id === userID).to[List].result)
  }

  def getUserID(email: String): Future[List[Int]] = {
    Logger.info("Getting user ID based on user E-mail")
    db.run(userQuery.filter(_.email === email).map(_.id).to[List].result)
  }

  def updateUser(updateUser: UpdateUserForm, id: Int): Future[Boolean] = {
    Logger.info("Updating user for given user ID")
    db.run(userQuery.filter(_.id === id).map(user => (user.firstName, user.middleName, user.lastName,
      user.phoneNo, user.gender, user.age)).update((updateUser.name.firstName, updateUser.name.middleName,
      updateUser.name.lastName, updateUser.phoneNumber, updateUser.gender, updateUser.age)))
      .map(_ > 0)
  }

  def checkEmailForUpdate(email: String, id: Int): Future[Boolean] = {
    Logger.info("Checking if email exists in database other than for the current user")
    val emailList = db.run(userQuery.filter(user => user.email === email && user.id =!= id).to[List].result)
    emailList.map { email =>
      if (email.isEmpty) true else false
    }
  }

  def updateUserByEmail(updatePassword: UpdatePassword): Future[Boolean] = {
    Logger.info("Updating password for given user")
    db.run(userQuery.filter(_.email === updatePassword.email).map(_.password).update(updatePassword.password)) map (_ > 0)
  }

  def getAllUsersWithStatus(id: Int): Future[Map[String, Boolean]] = {
    db.run(userQuery.filter(_.id =!= id).map(user => (user.email, user.isEnabled)).sorted.to[List].result).map(_.toMap)
  }

  def enableUser(email: String, status: Boolean): Future[Boolean] = {
    db.run(userQuery.filter(_.email === email).map(_.isEnabled).update(status)) map (_ > 0)
  }

  def getUserInfoForSession(email: String): Future[List[(Int, Boolean, Boolean)]] = {
    Logger.info("Getting user ID and isAdmin for given user email if he's enabled")
    db.run(userQuery.filter(_.email === email).map(user => (user.id, user.isAdmin, user.isEnabled)).to[List].result)

  }



}

trait UserTable extends HasDatabaseConfigProvider[JdbcProfile] {


  import driver.api._

  val userQuery: TableQuery[UserMapping] = TableQuery[UserMapping]

  class UserMapping(tag: Tag) extends Table[UserInfo](tag, "data") {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def firstName: Rep[String] = column[String]("firstname")

    def middleName: Rep[Option[String]] = column[Option[String]]("middlename")

    def lastName: Rep[String] = column[String]("lastname")

    def age: Rep[Int] = column[Int]("age")

    def email: Rep[String] = column[String]("email")

    def password: Rep[String] = column[String]("password")

    def street: Rep[String] = column[String]("street")

    def streetNo: Rep[Int] = column[Int]("streetno")

    def city: Rep[String] = column[String]("city")

    def gender: Rep[String] = column[String]("gender")

    def phoneNo: Rep[Long] = column[Long]("phoneno")

    def status: Rep[String] = column[String]("status")

    def isAdmin: Rep[Boolean] = column[Boolean]("isadmin")

    def isEnabled: Rep[Boolean] = column[Boolean]("isenabled")

    def * : ProvenShape[UserInfo] = (id, firstName, middleName, lastName, age, email, password,
      street, streetNo, city, gender, phoneNo, status, isAdmin, isEnabled) <> (UserInfo.tupled, UserInfo.unapply)

  }

  class AddressTable()

}
