package utils

import play.api.i18n._

import org.joda.time._
import org.joda.time.format._

object DateUtils {

	def format(millis: Long): String = {
		new DateTime(millis).toString("E MM/dd/yyyy HH:mm:ss")
	}
	
	def duration(millis: Long) = {
	  val fmt: PeriodFormatter = new PeriodFormatterBuilder()
        .printZeroAlways()
        .minimumPrintedDigits(2)
        .appendHours()
        .appendSeparator(" ")
        .printZeroAlways()
        .minimumPrintedDigits(2)
        .appendMinutes()
        .appendSeparator(" ")
        .printZeroAlways()
        .minimumPrintedDigits(2)
        .appendSeconds()
        .toFormatter();
	  
		fmt.print(new Period(millis))
	}
	
	def durationInSpans(millis: Long) = {
	  duration(millis).replaceAll("(\\d\\s?)", "<span>$1</span>")
	}
	
	def secondsSince(millis: Long): Int =
		new Duration(new DateTime(millis), new DateTime()).toStandardSeconds().getSeconds();
	
	def since(millis: Long): String = since(millis.toInt)
	
	def since(millis: Int): String = {
	  // TODO: use i18n
	  def and() = " " + "et" + " "
	  def suffix(token: String) = " " + Messages(token)
	  def plural(token: String) = "%ss".format(token)
	  
	  val day = suffix("jour")
	  val hour = suffix("heure")
	  val min = suffix("minute")
	  val sec = suffix("seconde")
	  
	  val dhm: PeriodFormatter = new PeriodFormatterBuilder()
	  .appendDays()
	  .appendSuffix(day, plural(day))
	  .appendSeparator(and())
	  .appendHours()
	  .appendSuffix(hour, plural(hour))
	  .appendSeparator(and())
	  .appendMinutes()
	  .appendSuffix(min, plural(min))
	  .appendSeparator(and())
	  .appendSeconds()
	  .appendSuffix(sec, plural(sec))
	  .toFormatter()
	  
	  dhm.print(new Period(millis))
	}
}