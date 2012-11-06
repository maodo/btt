 package controllers

import anorm._

import play.api._
import play.api.i18n._
import play.api.mvc._
import play.api.data._
import play.api.data.format.Formats._
import play.api.data.Forms._
import validation.Constraints._

import models._
import views._

object Admin extends Controller with Secured {

	//==========
	// Task
	//==========

	def tasks() = TODO


 	//==========
 	// User
 	//==========

	val userForm = Form(
		mapping(
			"id" -> ignored(NotAssigned:Pk[Long]),
		    "name" -> nonEmptyText.verifying(
			    		"admin.user.alreadyExists",
		    			name => validNameUserForm(name toString)),
		    "pwd" -> nonEmptyText,
		    "admin" -> boolean)
		(User.apply)(User.unapply)
 	)

 	def validNameUserForm(name: String) = {
	    val exists = User.findByName(name).isDefined;
	    Logger.debug(name + " exists " + exists);
	    !exists
 	}

 	def index = withUserAdmin { user => implicit request  =>
		Ok(html.admin.users(user, User.list(), userForm))
	}

	def users = withUserAdmin { user => implicit request  =>
		Ok(html.admin.users(user, User.list(), userForm))
	}

 	def newUser() = withUserAdmin { user => implicit request =>
 		var newUserName = ""
 		userForm.bindFromRequest.fold(
 			errors => BadRequest(html.admin.users(user, User.list(), errors)),
 		 	newUser => {
 		 		User.create(newUser); newUserName = newUser.name;
 		 		Redirect(routes.Admin.servers)
 		 	}
 		).flashing("userSuccess" -> Messages("admin.user.new.success", newUserName))
 	}

 	def updateUser(id: Long) = TODO /*IsAuthenticated { username => _ =>
 		Ok(views.html.adminIndex(User.list(), userForm.fill(User.findById(id).get), Server.list(), serverForm))
 	}*/

 	def deleteUser(id: Long) = withUserAdmin { user => implicit request =>
 		if (id != user.id.get) 
 			User.delete(id)

 		Redirect(routes.Admin.users)
 	}

 	//==========
 	// Server
 	//==========

 	val serverForm = Form(
 		mapping(
			"id" -> ignored(NotAssigned:Pk[Long]),
			"name" -> nonEmptyText,
			"active" -> boolean)
 		(Server.apply)(Server.unapply)
	)

	def servers() = withUserAdmin { user => implicit request =>
		Ok(html.admin.servers(user, Server.list(), serverForm))
	}

 	def newServer() = withUserAdmin { user => implicit request =>
 		serverForm.bindFromRequest.fold(
 			errors => BadRequest(html.admin.servers(user, Server.list(), errors)),
 			server => {
 				Server.create(server)
 				Redirect(routes.Admin.servers)
 			}
		).flashing("serverSuccess" -> Messages("admin.server.new.success"))
	}

	def deleteServer(id: Long) = TODO

}