package models

import java.util.Date

import anorm._
import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

import utils._

case class Task(id: Pk[Long], userId: Long, serverId: Long, startedAt: Date, duration: Int, failed: Boolean) {

	def since(): String = {
		DurationFormatter(DateUtils.secondsSince(startedAt))
	}
}

case class UserTask(user: User, task: Task, server: Server)
	
object Task {

	def apply(userId: Long, serverId: Long, startedAt: Date, duration: Int, failed: Boolean) = {
		new Task(NotAssigned, userId, serverId, startedAt, duration, failed)
	}

	def apply(userId: Long, serverId: Long) = {
		new Task(NotAssigned, userId, serverId, new Date(), 0, false)
	}

	val simple = {
		get[Pk[Long]]("task.id") ~
		get[Long]("task.userId") ~
		get[Long]("task.serverId") ~
		get[Date]("task.startedAt") ~
		get[Int]("task.duration") ~
		get[Boolean]("task.failed") map {
			case id~userId~buildId~startedAt~duration~failed =>
				Task(id, userId, buildId, startedAt, duration, failed)
		}
	}

	val withUserAndServer = Task.simple ~ User.simple ~ Server.simple map {
    	case task ~ user ~ server => (task, user, server)
  }

	def findByUserId(userId: Long, daysBefore: Int = -7): List[UserTask] = {
	  val now = new org.joda.time.DateTime()
	  val monday = new java.sql.Timestamp(now.withDayOfWeek(org.joda.time.DateTimeConstants.MONDAY).withHourOfDay(0).getMillis)
	  val sunday = new java.sql.Timestamp(now.withDayOfWeek(org.joda.time.DateTimeConstants.SUNDAY).withHourOfDay(23).getMillis)
	  
		DB.withConnection { implicit c =>
			SQL("""
				select t.*, u.*, s.*
				from task t
				join users u on u.id = t.userId
				join server s on s.id = t.serverId
			    where t.startedAt between {monday} and {sunday}
				order by t.startedAt desc
				"""
			)
			.on(
			    "daysBefore" -> daysBefore,
			    "monday" -> monday,
			    "sunday"-> sunday
			    )
			.as(withUserAndServer *) map (row => UserTask(row._2, row._1, row._3))
		}
	}

	def stop(id: Long) {
		DB.withConnection { implicit c =>
			SQL("update task set duration = datediff('second', startedAt, now()), failed = false where id = {id}")
			.on("id" -> id)
			.executeUpdate()
		}
	}

	def fail(id: Long) {
		DB.withConnection { implicit c =>
			SQL("update task set duration = datediff('second', startedAt, now()), failed = true where id = {id}")
			.on("id" -> id)
			.executeUpdate()
		}
	}

	def cancel(id: Long) {
		DB.withConnection { implicit c =>
			SQL("delete from task where id = {id}")
			.on("id" -> id)
			.executeUpdate()
		}
	}

	def create(task: Task) = {
		DB.withConnection { implicit c =>
			SQL("insert into task (userId, serverId, startedAt, duration, failed) values ({userId}, {serverId}, {startedAt}, {duration}, {failed})")
			.on(
				"userId" -> task.userId,
				"serverId" -> task.serverId,
				"startedAt" -> task.startedAt,
				"duration" -> task.duration,
				"failed" -> task.failed
				)
			.executeUpdate()
		}
	}


}