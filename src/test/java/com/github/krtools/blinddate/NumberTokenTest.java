package com.github.krtools.blinddate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

public class NumberTokenTest {

	@Test
	public void testSimpleNumbers() {
		validate("one", 1);
		validate("two", 2);
		validate("three", 3);
		validate("four", 4);
		validate("five", 5);
		validate("six", 6);
		validate("seven", 7);
		validate("eight", 8);
		validate("nine", 9);
		validate("ten", 10);
		validate("eleven", 11);
		validate("twelve", 12);
		validate("thirteen", 13);
		validate("fourteen", 14);
		validate("fifteen", 15);
		validate("sixteen", 16);
		validate("seventeen", 17);
		validate("eighteen", 18);
		validate("nineteen", 19);
		validate("twenty", 20);
		validate("thirty", 30);
	}

	@Test
	public void testSimpleNumbersPossessive() {
		validate("first", 1);
		validate("second", 2);
		validate("third", 3);
		validate("fourth", 4);
		validate("fifth", 5);
		validate("sixth", 6);
		validate("seventh", 7);
		validate("eighth", 8);
		validate("ninth", 9);
		validate("tenth", 10);
		validate("eleventh", 11);
		validate("twelfth", 12);
		validate("thirteenth", 13);
		validate("fourteenth", 14);
		validate("fifteenth", 15);
		validate("sixteenth", 16);
		validate("seventeenth", 17);
		validate("eighteenth", 18);
		validate("nineteenth", 19);
		validate("twentieth", 20);
		validate("thirtieth", 30);
	}

	@Test
	@Ignore
	public void testCompoundNumbers() {

	}

	@Test
	@Ignore
	public void testCompoundNumbersPossessive() {

	}

	@Test
	@Ignore
	public void testInvalidValues() {

	}

	private static void validate(String number, int value) {
		try {
			assertEquals(value, NumberTokenOps.getNumber(number));
		} catch (Throwable t) {
			fail(t.getMessage());
		}
	}

}
