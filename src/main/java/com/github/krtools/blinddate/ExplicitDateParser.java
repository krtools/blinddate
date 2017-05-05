package com.github.krtools.blinddate;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Class for parsing unambiguous date formats.
 * 
 * @author Kyle Rector
 */
public class ExplicitDateParser {

	private static final String[] EXPLICIT_MASKS = { "yyyy-MM-dd", //
			"yyyy/MM/dd", //
			"yyyy.MM.dd", //
			"yyyy MM dd", //

			"MM-dd-yyyy", //
			"MM/dd/yyyy", //
			"MM.dd.yyyy", //
			"MM dd yyyy", //

			"MM-dd-yyyy HH:mm:ss", //
			"MM/dd/yyyy HH:mm:ss", //

			"yyyy-MM-dd HH:mm:ss", //
			"yyyy/MM/dd HH:mm:ss", //

			"dd MMM yyyy", //
			"dd MMM yyyy HH:mm:ss", //

			"dd MMMM yyyy", //
			"dd MMMM yyyy HH:mm:ss", //

			"MMMM dd, yyyy", //
			"MMMM dd, yyyy hh:mm a", //

			"dd-MMM-yyyy", //
			"dd-MMMM-yyyy", //

			"yyyy-MM-dd HH:mm:ss.SSS ZZ", //
			"yyyy-MM-dd'T'HH:mm:ss.SSS ZZ" //
	};

	private static final DateTimeFormatter formatter = initFormatter(EXPLICIT_MASKS);

	private static DateTimeFormatter initFormatter(String[] dateMasks) {
		List<DateTimeParser> parsers = new ArrayList<DateTimeParser>();
		addOtherParsers(parsers);
		return getFormatter(dateMasks, parsers);
	}

	static DateTimeFormatter getFormatter(String[] dateMasks, List<DateTimeParser> parsers) {

		DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
		for (String mask : dateMasks) {
			parsers.add(DateTimeFormat.forPattern(mask).getParser());
		}
		builder.append(null, parsers.toArray(new DateTimeParser[parsers.size()]));
		return builder.toFormatter();
	}

	private static void addOtherParsers(List<DateTimeParser> parsers) {
		parsers.add(ISODateTimeFormat.dateTimeParser().getParser());
	}

	public static DateTime parse(String input) {
		try {
			DateTime dt = formatter.parseDateTime(input);
			if ((dt.getYear() < 1000 && dt.getYear() >= 0) || dt.getYear() > 9999) {
				return null;
			} else {
				return dt;
			}

		} catch (IllegalArgumentException iae) {
			return null;
		}
	}
}
