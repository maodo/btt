package models

import anorm._
import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

case class Server(id: Pk[Long], name: String, active: Boolean)

object Server {

	def apply(name: String) = new Server(NotAssigned, name, true)

	val simple = {
		get[Pk[Long]]("server.id") ~
		get[String]("server.name") ~
		get[Boolean]("server.active") map {
			case id~name~active => Server(id, name, active)
		}
	}

	def findIdByName(name: String) = {
		DB.withConnection { implicit c =>
			SQL("select id from server where name = {name}")
			.on("name" -> name)
			.as(scalar[Long].singleOpt)
			.get
		}
	}

	def list() = {
		DB.withConnection { implicit c =>
		  SQL("select * from server").as(simple *) 
		}
	}
	
	def listAsOptions(): List[(String, String)] = {
	  list() map (s => (s.id.toString, s.name))
	}

	def create(server: Server) {
		DB.withConnection { implicit c =>
			SQL("insert into server (name, active) values ({name}, {active})")
			.on(
				"name" -> server.name,
				"active" -> server.active
			)
			.executeUpdate()
		}
	}

	def delete(id: Long) {
		DB.withConnection { implicit c =>
			SQL("delete from server where id = {id}")
			.on("id" -> id)
			.executeUpdate()
		}
	}
}