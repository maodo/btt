import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import models._

class TestStatusModel extends Specification {

  "Status model" should {

    "list" in {
    	val status = Status.list
    	status must not beEmpty
    }

  }
}
