package controllers

import models.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import views._
import play.api.libs.iteratee.Iteratee

/**
 * Inspired by https://github.com/playframework/Play20/blob/master/samples/scala/zentasks
 */
object Login extends Controller {

  val loginForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "pwd" -> text) verifying (Messages("login.fail.notice"), result => result match {
        case (name, pwd) => User.authenticate(name, pwd).isDefined
      }))

  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.Tasks.index).withSession(Security.username -> user._1))
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Login.login).withNewSession.flashing(
      "success" -> Messages("logout.notice"))
  }

}

/**
 * Provide security features
 */
trait Secured {

  /**
   * Basic type for secured request.
   */
  type RequestUserSecure = User => Request[AnyContent] => Result;

  /**
   * Retrieve the connected user.
   */
  private def username(request: RequestHeader) = request.session.get(Security.username)

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Login.login)

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) {
      user => Action(request => f(user)(request))
    }
  }

  /**
   * Wrap the IsAuthenticated method to also fetch your user.
   */
  def withUser(f: RequestUserSecure) = IsAuthenticated { username =>
    implicit request =>
      User.findByName(username).map { user =>
        f(user)(request)
      }.getOrElse(onUnauthorized(request))
  }

  /**
   * Wrap the IsAuthenticated method to also fetch your user and check if its an admin.
   */
  def withUserAdmin(f: RequestUserSecure) = IsAuthenticated { username =>
    implicit request =>
      User.findByName(username).map { user =>
        if (user.admin) f(user)(request)
        else Results.Forbidden
      }.getOrElse(onUnauthorized(request))
  }

}
