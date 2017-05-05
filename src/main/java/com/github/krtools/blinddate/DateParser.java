package com.github.krtools.blinddate;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateParser {
	/**
	 * Attempts to parse a datetime expression. The time "context" is assumed to
	 * be the current date and time. This is consequential if the input given is
	 * ambiguous like 10/11/12 or textual like "today"
	 *
	 * @param input
	 * @return
	 */
	public static DateTime parse(String input) {
		return parse(input, null, null);
	}

	public static DateTime parse(String input, DateTimeZone timezone) {
		return parse(input, timezone, null);
	}

	public static DateTime parse(String input, String context) {
		return parse(input, null, context);
	}

	public static DateTime parse(String input, DateTimeZone timezone, String context) {
		if (input == null) {
			return null;
		}
		DateTime dateTimeContext = context != null ? parse(context) : DateTime.now();
		if (dateTimeContext == null) {
			return null;
		}

		DateTime dt;
		dt = ExplicitDateParser.parse(input);
		if (dt == null) {
			dt = AmbiguousDateParser.parse(input, dateTimeContext);
		}
		if (dt == null) {
			dt = TimestampDateParser.parse(input);
		}
		if (dt == null) {
			// save the slowest for last
			dt = NaturalLanguageDateParser.parse(input, dateTimeContext);
		}
		if (dt == null) {
			return null;
		} else {
			return dt.withZone(timezone);
		}
	}
	
	static List<DateTime> getPossibleDates(final String input) {
		List<DateTime> dts = new ArrayList<>();
		return dts;
	}
}
