import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import utils._

class TestDateUtils extends Specification {

  "DateUtils should" should {

    "convert millis duration to format hh mm ss" in {
      DateUtils.duration(6000) must equalTo("00 00 06")
      DateUtils.duration(60000) must equalTo("00 01 00")
      DateUtils.duration(3400000) must equalTo("00 56 40")
      DateUtils.duration(3600000) must equalTo("01 00 00")
    }
    
    "format seconds duration since a perido" in {
      DateUtils.since(1) must equalTo ("1 seconde")
      DateUtils.since(30) must equalTo ("30 secondes")
      DateUtils.since(60) must equalTo ("1 minute")
      DateUtils.since(75) must equalTo ("1 minute et 15 secondes")
      DateUtils.since(180) must equalTo ("3 minutes")
      DateUtils.since(3600) must equalTo ("1 heure")
      DateUtils.since(3660) must equalTo ("1 heure et 1 minute")
      DateUtils.since(3665) must equalTo ("1 heure et 1 minute et 5 secondes")
      DateUtils.since(86465) must equalTo ("1 jour et 1 minute et 5 secondes")
    }
    
    
  }
  
}