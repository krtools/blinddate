package com.github.krtools.blinddate;

import org.junit.Ignore;
import org.junit.Test;

public class AmbiguousDateParserTest extends AbstractDateParserTest {
	@Test
	public void testParseAmbiguousDates(){
		//mostly unambiguous, can easily know the year since it is out of month/day range
		validate("861008", null, t2);
		validate("100886", null, t2);
		validate("10081986", null, t2);
		validate("19861008", null, t2);
		validate("10/08/86", null, t2);
		validate("10/08/86 00:00:00", null, t2);
		validate("86/10/08 00:00:00", null, t2);
		validate("08 Oct 86", null, t2);
		validate("08 October 86", null, t2);
		validate("08 October 86 00:00:00", null, t2);
		validate("October 8, 86", null, t2);
	}

	@Test
	public void testAmbiguousWithContexts() {
		//clearly ambiguous formats, pick date closest to context
		final String oldContext = "2004-01-01";
		final String newContext = "2014-06-01";

		validate("06/04/14", oldContext, ymd(2006, 04, 14));
		validate("06/04/14", newContext, ymd(2014, 06, 04));

		final String context4 = "2011-01-01";
		final String context5 = "2014-01-01";
		final String context6 = "2009-01-01";

		validate("11/10/12", context4, ymd(2011, 10, 12));
		validate("10/12/11", context4, ymd(2010, 12, 11));
		validate("111012", context4, ymd(2011, 10, 12));
		validate("101211", context4, ymd(2010, 12, 11));
		//next context
		validate("11/10/12", context5, ymd(2012, 11, 10));
		validate("10/12/11", context5, ymd(2011, 10, 12));
		validate("111012", context5, ymd(2012, 11, 10));
		validate("101211", context5, ymd(2011, 10, 12));
		//next context
		validate("11/10/12", context6, ymd(2011, 10, 12));
		validate("10/12/11", context6, ymd(2010, 12, 11));
		validate("111012", context6, ymd(2011, 10, 12));
		validate("101211", context6, ymd(2010, 12, 11));
	}

	@Test
	@Ignore
	public void testAmbiguousDatesWithTimezones(){
		//TODO Add timezones to ambig date tests
	}

	@Test
	public void testInvalidValues() {
		validate("111", null, null);
		validate("01", null, null);
		validate("05jun04", null, null);
	}

}
