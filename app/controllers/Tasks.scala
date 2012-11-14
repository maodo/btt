package controllers

import play.api._
import play.api.i18n._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Tasks extends Controller with Secured {

  val newTaskForm = Form(
    single(
      "server" -> longNumber
    )
  )
  
  def index = withUser { user => _ => 
  	val Tasks = Task.listForWeek
  	val TasksByUsers = Tasks groupBy (_.user.id == user.id)
  	val totalDuration = Task.totalDurations

  	// TODO: maybe do the flat shit in an other place
    Ok(html.tasks(
		user,
		totalDuration,
		newTaskForm,
		Server.listAsOptions(),
		TasksByUsers getOrElse(true, List()),
		TasksByUsers getOrElse(false, List()))
    )
  }

  def start = withUser { user => implicit request => 
    newTaskForm.bindFromRequest.fold(
      formWithErrors => Redirect(routes.Tasks.index),
      serverId => {
        Task.create(Task(user.id.get, serverId))
        Redirect(routes.Tasks.index)
      }
    )
  }

  def stop(id: Long) = withUser { user => _ => 
    Task.stop(id)
    Redirect(routes.Tasks.index)
  }

  def fail(id: Long) = withUser { user => u =>
    Task.fail(id)
    Redirect(routes.Tasks.index)
  }

 def cancel(id: Long) = withUser { user => u =>
    Task.cancel(id)
    Redirect(routes.Tasks.index)
  }  

}