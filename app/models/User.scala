package models
	
import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current
import play.api.libs.Crypto

case class User(id: Pk[Long] = NotAssigned, name: String, pwd: String, admin: Boolean)

object User {

	def apply(name: String, pwd: String, admin: Boolean) = new User(NotAssigned, name, pwd, admin)

	val simple = {
		get[Pk[Long]]("user.id") ~
		get[String]("user.name") ~
		get[String]("user.pwd") ~ 
		get[Boolean]("user.admin") map {
			case id~name~pwd~admin => User(id, name, pwd, admin)
		}
	}

	def authenticate(name: String, pwd: String): Option[User] = {
		DB.withConnection { implicit c =>
			SQL("select * from User where name = {name} and pwd = {pwd}")
			.on("name" -> name,
				"pwd" -> encrypt(pwd))
			.as(User.simple.singleOpt)
		}
	}

	def findByName(name: String): Option[User] = {
		DB.withConnection { implicit c =>
			SQL("select * from User where name = {name}")
			.on("name" -> name)
			.as(User.simple.singleOpt)
		}
	}

	def findIdByName(name: String): Long = {
		DB.withConnection { implicit c =>
			SQL("select id from User where name = {name}")
			.on("name" -> name)
			.as(scalar[Long].singleOpt) // => Option 
			.get
		}
	}

	def findById(id: Long): Option[User] = {
		DB.withConnection { implicit c =>
			SQL("select * from User where id = {id}")
			.on("id" -> id)
			.as(User.simple.singleOpt)
		}
	}

	def list(): List[User] = DB.withConnection { implicit c => SQL("select * from user order by name").as(simple *) }

	def create(user: User) {
		DB.withConnection { implicit c =>
    		SQL("insert into user (name, pwd, admin) values ({name}, {pwd}, {admin})").
			on("name" -> user.name,
	  			"pwd" -> encrypt(user.pwd),
	  			"admin" -> user.admin).
			executeUpdate()
 		 }
	}

	def delete(id: Long) {
		DB.withConnection { implicit c => 
			SQL("delete from user where id = {id}")
			.on("id" -> id)
			.executeUpdate()
		}
	}
	
	//===================================
	// Helper methods
	//===================================

	def encrypt(password: String) = Crypto.sign(password)
	
}
