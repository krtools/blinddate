package com.github.krtools.blinddate;

import org.junit.Ignore;
import org.junit.Test;

public class ExplicitDateParserTest extends AbstractDateParserTest {
	@Test
	public void testExplicitDates() {
		validate("1986-10-08", null, t2);
		validate("1986/10/08", null, t2);
		validate("1986.10.08", null, t2);
		validate("1986 10 08", null, t2);

		validate("10-08-1986", null, t2);
		validate("10/08/1986", null, t2);
		validate("10.08.1986", null, t2);
		validate("10 08 1986", null, t2);

		validate("10-08-1986 00:00:00", null, t2);
		validate("10/08/1986 00:00:00", null, t2);

		validate("1986-10-08 00:00:00", null, t2);
		validate("1986/10/08 00:00:00", null, t2);

		validate("08 Oct 1986", null, t2);
		validate("08 October 1986", null, t2);
		validate("08 October 1986 00:00:00", null, t2);

		validate("October 8, 1986", null, t2);

		validate("8-Oct-1986", null, t2);
		validate("8-October-1986", null, t2);

		validate("1986-10-08T00:00:00.000", null, t2);
	}

	@Test
	@Ignore
	public void testExplicitDatesWithTimezones(){
		//TODO add timezones to explicit date tests
	}

	@Test
	public void testInvalidValues() {
		validate("1986-10.08", null, null);
		validate("1986-1008", null, null);
		validate("1986 10.08", null, null);
		validate("1986\\10\\08", null, null); //backslashes are just weird in dates
		validate("1986\\10\\08 at midnight", null, null); //backslashes are just weird in dates
	}
}
