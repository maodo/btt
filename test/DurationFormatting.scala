import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import utils.DurationFormatter

class DurationFormatting extends Specification {

  "The DurationFormatting" should {

    "format seconds duration" in {
      DurationFormatter(1) must equalTo ("1 seconde")
      DurationFormatter(30) must equalTo ("30 secondes")
      DurationFormatter(60) must equalTo ("1 minute")
      DurationFormatter(75) must equalTo ("1 minute et 15 secondes")
      DurationFormatter(180) must equalTo ("3 minutes")
      DurationFormatter(3600) must equalTo ("1 heure")
      DurationFormatter(3660) must equalTo ("1 heure et 1 minute")
      DurationFormatter(3665) must equalTo ("1 heure et 1 minute et 5 secondes")
      DurationFormatter(86465) must equalTo ("1 jour et 1 minute et 5 secondes")
    }
  }
}