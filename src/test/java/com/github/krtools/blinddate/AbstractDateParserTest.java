package com.github.krtools.blinddate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.github.krtools.blinddate.DateParser;

public abstract class AbstractDateParserTest {

	protected final String context1 = "2014-06-01";
	protected final DateTime t1 = ymd(2014, 6, 1);

	protected final String context2 = "1986-10-08";
	protected final DateTime t2 = ymd(1986, 10, 8);

	protected final String context3 = "2014-06-11 16:45:32"; // Wednesday June
																// 11, 2014
	protected final DateTime t3 = new DateTime(2014, 6, 11, 16, 45, 32);



	protected final String[] contexts = {
			"2014-12-31 05:30:01",
			"2014-01-01 05:30:01",
			"2015-01-01 05:30:01",
			"2016-02-29 05:30:01"
	};

	protected final DateTime[] ts = {
			ymdhms(2014, 12, 31, 5, 30, 1),
			ymdhms(2014, 01, 01, 5, 30, 1),
			ymdhms(2015, 01, 01, 5, 30, 1),
			ymdhms(2016, 02, 29, 5, 30, 1)
	};

	protected final long maxInt = Integer.MAX_VALUE;

	protected void validate(String input, DateTimeZone timezone, String context, DateTime expected){
		DateTime dt = null;
		try {
			dt = DateParser.parse(input, context);
		} catch (Throwable e) {
			e.printStackTrace();
			fail("parsing did not fail gracefully, this should never happen.");
		}
		// junit won't show comparison failure in ui with DateTime's
		assertEquals("[@" + context + "]", expected, dt);
	}

	protected void validate(String input, String context, DateTime expected) {
		validate(input, null, context, expected);
	}

	protected static DateTime ymd(int year, int month, int day) {
		return new DateTime(year, month, day, 0, 0);
	}

	protected static DateTime ymdhms(int year, int month, int day, int hour,
			int minute, int second) {
		return new DateTime(year, month, day, 0, 0, 0);
	}
}
