package models

import scala.concurrent.ExecutionContext.Implicits.global
import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.Future

case class UserInfo(id: Int, firstName: String, middleName: Option[String], lastName: String,
                    age: Int, email: String, password: String, street: String, streetNo: Int, city: String,
                    gender: String, phoneNumber: Long, status: String)

class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserTable {

  import driver.api._

  def addNewUser(user: UserInfo): Future[Boolean] = {
    db.run(userQuery += user).map(_ > 0)
  }
  def findByEmail (email: String): Future[Option[String]] = {
    val query = userQuery.filter(_.email === email).map(_.email).result.headOption
    db.run(query)
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

    def * : ProvenShape[UserInfo] = (id, firstName, middleName, lastName, age, email, password,
      street, streetNo, city, gender, phoneNo, status) <> (UserInfo.tupled, UserInfo.unapply)

  }

  class AddressTable()

}
