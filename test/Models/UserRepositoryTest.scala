package models

import controllers.{Name, UpdatePassword, UpdateUserForm, UserAddress}
import org.scalatestplus.play.PlaySpec

class UserRepositoryTest extends PlaySpec {

  val userRepo = new ModelsTest[UserRepository]

  val user = UserInfo(2, "Ayush", None, "Aggarwal", 21, "ayush@knoldus.com", "$2a$10$y981gqfa08fwWpojVudnKuwnJSt5lkYhr1IHEIMXUb9sN42usQ4Tu",
    "gali", 2, "delhi", "male", 9999819877L, "single", false, true)

  val updateUserForm = UpdateUserForm(Name("Ayush", None, "Aggarwal"), 21, "ayush@knoldus.com",
    UserAddress("gali", 2, "delhi"), "male", 9999819877L, List(1, 3))

  val updatePassword = UpdatePassword("ayush@knoldus.com", "ayush1", "ayush1")

  "User Repository" should {

    "be able to add a user" in {

      val result = userRepo.result(userRepo.repository.addNewUser(user))
      result mustEqual true
    }

    "return false if email exists" in {

      val result = userRepo.result(userRepo.repository.checkEmail("ayush@knoldus.com"))
      result mustEqual false
    }

    "return true if email does not exist" in {

      val result = userRepo.result(userRepo.repository.checkEmail("not@exists.com"))
      result mustEqual true
    }

    "return true if user exists in Database" in {

      val result = userRepo.result(userRepo.repository.checkIfUserExists("ayush@knoldus.com", "ayush1"))

      result mustEqual false
    }

    "return false if user does not exist in Database" in {

      val result = userRepo.result(userRepo.repository.checkIfUserExists("akshansh95@knoldus.com", "ayush1"))

      result mustEqual false
    }

    "return false if user entered wrong password" in {

      val result = userRepo.result(userRepo.repository.checkIfUserExists("ayush@knoldus.com", "ayush1"))

      result mustEqual false
    }

    "return user by ID" in {

      val result = userRepo.result(userRepo.repository.getUserByID(2))

      result mustEqual UserInfo(2, "Ayush", None, "Aggarwal", 21, "ayush@knoldus.com", "$2a$10$y981gqfa08fwWpojVudnKuwnJSt5lkYhr1IHEIMXUb9sN42usQ4Tu",
        "gali", 2, "delhi", "male", 9999819877L, "single", false, true)
    }

    "return userID" in {

      val result = userRepo.result(userRepo.repository.getUserID("ayush@knoldus.com"))

      result mustEqual List(2)
    }

    "update user" in {

      val result = userRepo.result(userRepo.repository.updateUser(updateUserForm, 2))

      result mustEqual true
    }

    "update password for the user" in {

      val result = userRepo.result(userRepo.repository.updateUserByEmail(updatePassword))

      result mustEqual true
    }

    "return all the users with the enable/disable status" in {

      val result = userRepo.result(userRepo.repository.getAllUsersWithStatus(1))

      result mustEqual Map("ayush@knoldus.com" -> true)
    }

    "Disable a user in database" in {

      val result = userRepo.result(userRepo.repository.enableUser("ayush@knoldus.com", false))

      result mustEqual true
    }

    "return user information for session" in {

      val result = userRepo.result(userRepo.repository.getUserInfoForSession("ayush@knoldus.com"))

      result mustEqual List((2, false, false))
    }
  }

}