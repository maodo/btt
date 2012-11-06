package utils

import play.api.i18n._

import org.joda.time._
import org.joda.time.format._

object DurationFormatter {

	// TODO: use i18n
	def apply(duration: Int) = {

		def and() = " " + "et" + " "
		def suffix(token: String) = " " + Messages(token)
		def plural(token: String) = "%ss".format(token)

		val s1 = Seconds.seconds(duration)
		val p1 = new Period(s1)

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

    	dhm.print(p1.normalizedStandard())
	}

}