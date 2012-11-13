package models

import java.util.Date

import anorm._
import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

import utils._

object Status extends Enumeration {
	
   val ENDED 		= Value(1, "ended")
   val FAILED 		= Value(2, "failed")
   val CANCELLED 	= Value(3, "cancelled")
   
   def list = values

}
