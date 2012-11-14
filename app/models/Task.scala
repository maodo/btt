package models

import java.util.Date

import anorm._
import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

import utils._

case class Task(id: Pk[Long], userId: Long, serverId: Long, startedAt: Long, duration: Int, failed: Boolean) {

  def since(): String = {
    DateUtils.since(DateUtils.secondsSince(startedAt))
  }
  
  def millisElapsed(): Long = {
    System.currentTimeMillis() - startedAt
  }
}

case class UserTask(user: User, task: Task, server: Server)

object Task {

  def apply(userId: Long, serverId: Long, startedAt: Long, duration: Int, failed: Boolean) = {
    new Task(NotAssigned, userId, serverId, startedAt, duration, failed)
  }

  def apply(userId: Long, serverId: Long) = {
    new Task(NotAssigned, userId, serverId, System.currentTimeMillis(), 0, false)
  }

  val simple = {
    get[Pk[Long]]("task.id") ~
      get[Long]("task.userId") ~
      get[Long]("task.serverId") ~
      get[Long]("task.startedAt") ~
      get[Int]("task.duration") ~
      get[Boolean]("task.failed") map {
        case id ~ userId ~ buildId ~ startedAt ~ duration ~ failed =>
          Task(id, userId, buildId, startedAt, duration, failed)
      }
  }

  val withUserAndServer = Task.simple ~ User.simple ~ Server.simple map {
    case task ~ user ~ server => (task, user, server)
  }
  
  def totalDurations: Long = {
    val values = DB.withConnection { implicit c =>
      SQL("select t.* from task t")
        .as(simple *)
    }

    val groups = values.groupBy(_.duration > 0)
    val result = groups.map { g =>
      g._1 match {
        case true => {
          g._2.map(_.duration).toList.sum
        }
        case false => {
          g._2.map(_.millisElapsed).toList.sum
        }
      }
    }

    result.sum
  }

  // TODO: add week parameter.
  def listForWeek(): List[UserTask] = {
    val now = new org.joda.time.DateTime()
    val mondayMillis = now.withDayOfWeek(org.joda.time.DateTimeConstants.MONDAY).withHourOfDay(0).getMillis
    val sundayMillis = now.withDayOfWeek(org.joda.time.DateTimeConstants.SUNDAY).withHourOfDay(23).getMillis
    DB.withConnection { implicit c =>
      SQL("""
				select t.*, u.*, s.*
				from task t
				join users u on u.id = t.userId
				join server s on s.id = t.serverId
			    where t.startedAt between {monday} and {sunday}
				order by t.startedAt desc
				""")
        .on(
          "monday" -> mondayMillis,
          "sunday" -> sundayMillis)
        .as(withUserAndServer *) map (row => UserTask(row._2, row._1, row._3))
    }
  }

  def stop(id: Long) {
    end(id, failed = false)
  }

  def fail(id: Long) {
    end(id, failed = true)
  }

  private def end(id: Long, failed: Boolean) {
    DB.withConnection { implicit c =>
      SQL("update task set duration = {endedAt} - startedAt, failed = {failed} where id = {id}")
        .on("id" -> id,
          "failed" -> failed,
          "endedAt" -> System.currentTimeMillis())
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
          "failed" -> task.failed)
        .executeUpdate()
    }
  }

}