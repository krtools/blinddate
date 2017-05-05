package com.github.krtools.blinddate;

import java.util.regex.Pattern;

import org.joda.time.DateTime;

public class TimestampDateParser {

	private static final Pattern number = Pattern.compile("\\d{9,19}");

	private static final Pattern seconds = Pattern.compile(tsRegex(9));
	private static final Pattern milliseconds = Pattern.compile(tsRegex(12));
	private static final Pattern nanoseconds = Pattern.compile(tsRegex(18));

	/**
	 * Will attempt to parse a date that is of a timestamp format in seconds,
	 * milliseconds, or nanoseconds. It will not parse seconds under 9 digits, 
	 * milliseconds under 12 digits, or nanoseconds under 18 digits.
	 * 
	 * @param input
	 * @return
	 */
	public static DateTime parse(String input) {
		if (input != null && number.matcher(input).matches()) {
			DateTime dt = null;
			dt = parse(input, seconds, Integer.MAX_VALUE, 1000L, true);
			if (dt == null) {
				dt = parse(input, milliseconds, -1, 1L, true);
			}
			if (dt == null) {
				dt = parse(input, nanoseconds, -1, 1000000L, false);
			}
			return dt;
		} else {
			return null;
		}
	}

	private static DateTime parse(String input, Pattern pattern, long upperLimit, long scale,
			boolean multiply) {
		if (pattern.matcher(input).matches()) {
			long parsed = Long.parseLong(input);
			if(upperLimit != -1 && parsed > upperLimit){
				return null;
			}		
			long time = multiply ? parsed * scale : Long
					.parseLong(input) / scale;
			return new DateTime(time);
		} else {
			return null;
		}
	}

	private static String tsRegex(int numdigits) {
		return "[12]\\d{" + numdigits + "}|\\d{" + numdigits + "}";
	}
}
