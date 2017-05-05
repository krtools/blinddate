package com.github.krtools.blinddate;

import org.junit.Test;

public class DateParserTest extends AbstractDateParserTest {

	@Test
	public void testContexts() {
		// validate that contexts are being parsed correctly
		validate(context1, null, t1);
		validate(context2, null, t2);
		validate(context3, null, t3);
	}

	@Test
	public void testInvalidValues() {
		// no NPE or weird errors.
		validate(null, null, null);
		validate("1008--1", null, null);
		validate("10---0-0", null, null);
		validate("aa/bb/cccc", null, null);
		validate("????", null, null);
		validate("asdf", "-asdf", null);
		validate("", "", null);
	}

	@Test
	public void testValidValuesInvalidContexts() {
		// no NPE or weird errors.
		validate("01/02/2016", "", null);
		validate("01/02/2016", "0", null);
		validate("01/02/2016", "?", null);
		validate("01/02/2016", "=(*%^&*(", null);
		validate("01/02/2016", "-", null);
	}

}