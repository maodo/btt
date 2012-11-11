import java.util.Date

import play.api._

import models._
import anorm._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    InitialData.insert()
  }

}

/**
 * Initial set of data to be imported
 * in the sample application.
 */
object InitialData {

  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

  def insert() = {

    if (User.list.isEmpty) {

      Seq(
        User("root", "chichi", true),
        User("test", "test", false)).foreach(User.create)

      Seq(
        Server("Exo Platform 3.5"),
        Server("Exo Platform 3.0"),
        Server("Jboss 5"),
        Server("Jboss 4"),
        Server("Tomcat 6"),
        Server("Glassfish 2.x")).foreach(Server.create)

      val userTest = User.findIdByName("test")
      Seq(
        Task(userTest, Server.findIdByName("Jboss 5"), System.currentTimeMillis(), 120, false),
        Task(userTest, Server.findIdByName("Jboss 4"), System.currentTimeMillis(), 60, false),
        Task(userTest, Server.findIdByName("Jboss 5")),
        Task(userTest, Server.findIdByName("Jboss 5")),
        Task(userTest, Server.findIdByName("Jboss 5"))
      ).foreach(Task.create)
    }
  }

}