package utils

import java.util.Date
import org.joda.time._

object DateUtils {
	
	def secondsSince(startDate: Date): Int =
		new Duration(new DateTime(startDate.getTime()), new DateTime()).toStandardSeconds().getSeconds();

	def format(date: Date): String = {
		new DateTime(date.getTime()).toString("E MM/dd/yyyy HH:mm:ss")
	}
}