import org.specs2.mutable.Specification

import play.api.test._
import play.api.test.Helpers._

import models._

class TestServerModel extends Specification {

  "Server model" should {

    "retrieved id by name" in {
      running(FakeApplication()) {
        val serverId = Server.findIdByName("Jboss 5")
        serverId must be equalTo (3)
      }
    }

    "list servers" in {
      running(FakeApplication()) {
        val servers = Server.list
        servers must not be empty
        servers must be size (6)
      }
    }

    "list as options for select html" in {
      running(FakeApplication()) {
    	 val options = Server.listAsOptions
    	 options must not be empty
    	 options must be size(6)
    	 
    	 options(0)._1 must be equalTo(1.toString)
    	 options(0)._2 must be equalTo("Exo Platform 3.5")
      }
    }
    
    "create server" in {
      running(FakeApplication()) {
    	  Server.create(Server("toto"))
    	  
    	  val serverId = Server.findIdByName("toto")
    	  serverId.toInt must be_>(2)
      }
    }
    
    "delete server by id" in {
      running(FakeApplication()) {
    	  val listSize = Server.list.size
    	  Server.delete(1)
    	  listSize.toInt must be_>(Server.list.size.toInt)
      }
    }
  }

}