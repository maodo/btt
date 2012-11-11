import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import models._

class TestUserModel extends Specification {

  "User model" should {

    "be authenticated" in {
      running(FakeApplication()) {
        val Some(user) = User.authenticate("root", "chichi")
        user.name must equalTo("root")
        user.admin must beTrue
      }
    }

    "be retrieved by name" in {
      running(FakeApplication()) {

        val Some(user) = User.findByName("root")
        user.name must equalTo("root")
        user.admin must beTrue
      }
    }

    "be retrieved by id" in {
      running(FakeApplication()) {
        val Some(user) = User.findById(1)
        user.name must equalTo("root")
        user.admin must beTrue
      }
    }

    "be retrieved id by name" in {
      running(FakeApplication()) {

        val userId = User.findIdByName("root")
        userId must equalTo(1)
      }
    }

    "list users" in {
      running(FakeApplication()) {
        val list = User.list
        list must not be empty
        list must have size (2)
      }
    }

    "delete user by id" in {
      running(FakeApplication()) {
        User.delete(1)
        User.findById(1) must beNone
      }
    }
    
    "create user" in {
      running(FakeApplication()) {
    	  User.create(User("toto", "titi", false))
    	  val Some(user) = User.findByName("toto")
    	  user.name must equalTo("toto")
    	  user.admin must beFalse
      }
    }

  }
}
