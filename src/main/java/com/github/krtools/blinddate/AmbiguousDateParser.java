package com.github.krtools.blinddate;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class AmbiguousDateParser {

	private static final String[] AMBIGUOUS_MASKS = { //
			"yy-MM-dd", //
			"yy/MM/dd", //
			"yy.MM.dd", //
			"yy MM dd", //

			"MM-dd-yy", //
			"MM/dd/yy", //
			"MM.dd.yy", //
			"MM dd yy", //

			"MM-dd-yy HH:mm:ss", //
			"MM/dd/yy HH:mm:ss", //
			"yy-MM-dd HH:mm:ss", //
			"yy/MM/dd HH:mm:ss", //

			"dd MMM yy", //
			"dd MMM yy HH:mm:ss", //

			"dd MMMM yy", //
			"dd MMMM yy HH:mm:ss", //

			"dd-MMM-yy", //
			"dd-MMMM-yy", //

			"MMMM dd, yy", //
			"MMMM dd, yy hh:mm a", //

			"yyMMdd", //

			"yyyyMMdd", //

			"MMddyy", //
			"MMddyyyy", //

			"dd-mm-yyyy", //
			"dd/mm/yyyy", //
			"dd.mm.yyyy", //
			"dd mm yyyy" //
	};

	private static final List<DateTimeFormatter> formatters = initFormatter(AMBIGUOUS_MASKS);

	/**
	 * Attempts to parse a date with potentially ambiguous formats and, if
	 * multiple are valid, chooses the date that is closest to the time context
	 * (by default, the current date and time)
	 * 
	 * @param input
	 * @param dateTimeContext
	 * @return
	 */
	public static DateTime parse(String input, DateTime dateTimeContext) {
		DateTime retDate = null;
		long retDistFromNow = Long.MAX_VALUE;
		long now = dateTimeContext.getMillis();
		List<DateTime> dts = new ArrayList<DateTime>();
		for (DateTimeFormatter formatter : formatters) {
			try {
				final DateTime dt = formatter.parseDateTime(input);
				dts.add(dt);
				long dtDist = now - dt.getMillis();
				dtDist = dtDist >= 0 ? dtDist : -dtDist;
				if (dtDist < retDistFromNow) {
					retDate = dt;
					retDistFromNow = dtDist;
				}
			} catch (IllegalArgumentException iae) {
			}
		}
		if (retDate == null || (retDate.getYear() < 1000 && retDate.getYear() >= 0) || retDate.getYear() > 9999) {
			return null;
		} else {
			return retDate;
		}
	}

	private static List<DateTimeFormatter> initFormatter(String[] dateMasks) {
		List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
		for (String mask : dateMasks) {
			formatters.add(DateTimeFormat.forPattern(mask));
		}
		return formatters;
	}
}
