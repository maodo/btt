package utils

import java.util.Date
import org.joda.time._

object DateUtils {
	
	def secondsSince(millis: Long): Int =
		new Duration(new DateTime(millis), new DateTime()).toStandardSeconds().getSeconds();

	def format(millis: Long): String = {
		new DateTime(millis).toString("E MM/dd/yyyy HH:mm:ss")
	}
}