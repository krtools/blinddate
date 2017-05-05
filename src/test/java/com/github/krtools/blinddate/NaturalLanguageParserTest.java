package com.github.krtools.blinddate;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

public class NaturalLanguageParserTest extends AbstractDateParserTest {

	@Ignore
	public void testParseNaturalLanguageStrings(){
		/**
		 * Notes:
		 *
		 * Make the DateVisitorImpl have one method where it determines the time based on a Token, that way it is easier to integrate with the other rules
		 * (i guess i mean if you have a time rule like "at midnight", every alternative should reference the same rule so that it is easier to maintain
		 *
		 */

		//TODO process numbers like 13th and fifteenth (for stuff with pretentious language like wedding invitations)
		//TODO convert everything to a localdate? not dealing with time zones at the moment
	}

	@Ignore
	@Test
	public void testIncrementalPartial(){
		validate("the next april 15", context3, t3);
		validate("the next august 18", context3, t3);
	}

	@Ignore
	@Test
	public void testCalculateIncrementalPartialDateOccurence(){
		//TODO calculating occurrence of a partial date/time, possibly before/after/within a relative date
		validate("the next friday the 13th", context3, t3);
		validate("the first friday the 13th after the beginning of next year", context3, t3);
	}

	@Ignore
	@Test
	public void testForwardReferenceTimeunit(){//TODO implement forward referencing
		//named timeunits adding to dates with relative startings dates, need to forward reference the timeunit
		validate("beginning of a month ago", context3, ymd(2014,4,10));
		validate("2 weeks before the beginning of a month ago", context3, ymd(2014,4,10));
		validate("2 fridays before the beginning of a month ago", context3, ymd(2014,4,10));
	}

	@Ignore
	@Test
	public void testCountTimeunitsFromStartingDate(){
		//TODO calculating occurrence of timeunit before, after, or within an incremented date
		validate("third saturday of this month", context3, t3);
		validate("third satuday of last june", context3, t3);

		//TODO calculate occurrence of timeunit with a starting date (floor or timeunit of actual date)
		validate("the first monday on or after the next april 15", context3, t3);
		validate("fourth thursday of november", context3, t3);
	}

	@Test
	public void testIncrementFromNamedTimeunitWithinParent(){
		validate("3 days before friday of last week", context3, t3.minusWeeks(1).withDayOfWeek(2).dayOfWeek().roundFloorCopy());
		//some mixing of named timeunits
		validate("3 days before the friday of last week", context3, t3.minusWeeks(1).withDayOfWeek(2).dayOfWeek().roundFloorCopy());
		validate("3 days before the friday of the last week", context3, t3.minusWeeks(1).withDayOfWeek(2).dayOfWeek().roundFloorCopy());
	}

	@Test
	public void testNamedTimeunitWithinParent() {
		//reference named timeunit within a time range containing that timeunit (once)
		validate("friday of last week", context3, t3.minusWeeks(1).withDayOfWeek(5).dayOfWeek().roundFloorCopy());
		validate("june of next year", context3, t3.plusYears(1).monthOfYear().roundFloorCopy());
		validate("september of last year", context3, t3.minusYears(1).withMonthOfYear(9).monthOfYear().roundFloorCopy());
		validate("next week on thursday", context3, t3.plusWeeks(1).withDayOfWeek(4).dayOfWeek().roundFloorCopy());
	}

	@Test
	public void testTimeunitBackreference() {
		//missing timeunit backreferencing the previous timeunit
		validate("the friday before last", context3, t3.minusWeeks(2).withDayOfWeek(5).dayOfWeek().roundFloorCopy());
		validate("the week after next", context3, t3.plusWeeks(2).weekOfWeekyear().roundFloorCopy());
		validate("the week before next", context3, t3.weekOfWeekyear().roundFloorCopy()); //this week, monday @ midnight
		validate("2 fridays before last", context3, t3.minusWeeks(3).withDayOfWeek(5).dayOfWeek().roundFloorCopy());
		validate("2 months before last", context3, t3.minusMonths(3).monthOfYear().roundFloorCopy());
		validate("a day before the last", context3, t3.minusDays(2).dayOfYear().roundFloorCopy());
		validate("2 months before 2 months before last", context3, t3.minusMonths(5).monthOfYear().roundFloorCopy());
		validate("an hour after last", context3, t3.hourOfDay().roundFloorCopy());
		validate("a week before the previous", context3, t3.minusWeeks(2).weekOfWeekyear().roundFloorCopy());
		validate("a week before the previous on wednesday", context3, t3.minusWeeks(2).weekOfWeekyear().roundFloorCopy().plusDays(2));
	}

	@Test
	public void testAbsolutes() {
		//absolutes
		validate("today", null, DateTime.now().withTimeAtStartOfDay());
		validate("NOW", context3, t3);
		validate("yesterday", context3, t3.minusDays(1).withTimeAtStartOfDay());
		validate("tomorrow", context3, t3.plusDays(1).withTimeAtStartOfDay());
	}

	@Test
	public void testAbsolutesWithTimes() {
		//absolutes
		validate("today at noon", null, DateTime.now().withTimeAtStartOfDay().plusHours(12));
		validate("NOW at noon", context3, t3.withTime(12, 0, 0, 0));
		validate("yesterday at midnight", context3, t3.minusDays(1).withTimeAtStartOfDay());
		validate("tomorrow at midnight", context3, t3.plusDays(1).withTimeAtStartOfDay());
	}

	@Test
	public void testFloorCeilingIncremented() {
		//floor or ceiling of a timeunit
		//should 'end of the week' be the last day of the week, start of day or just last instant of the week?
		validate("the beginning of next month", context3, t3.plusMonths(1).monthOfYear().roundFloorCopy());
		validate("the beginning of last month", context3, t3.plusMonths(-1).monthOfYear().roundFloorCopy());
		validate("the beginning of this month", context3, t3.plusMonths(0).monthOfYear().roundFloorCopy());
		validate("the end of next week", context3, t3.plusWeeks(1).dayOfWeek().roundFloorCopy().withDayOfWeek(7));
		validate("the end of last week", context3, t3.plusWeeks(-1).dayOfWeek().roundFloorCopy().withDayOfWeek(7));
		validate("the end of this week", context3, t3.plusWeeks(0).dayOfWeek().roundFloorCopy().withDayOfWeek(7));
		validate("the end of august", context3, t3.plusMonths(2).monthOfYear().roundFloorCopy().dayOfMonth().withMaximumValue());
		validate("the beginning of the week", context3, t3.weekOfWeekyear().roundFloorCopy());
		validate("the end of the next hour", context3, t3.plusHours(1).hourOfDay().roundFloorCopy().withMinuteOfHour(59));
		validate("the end of the week", context3, t3.plusDays(4).withTimeAtStartOfDay());
		validate("the beginning of this past week", context3, t3.minusWeeks(1).withDayOfWeek(1).withTimeAtStartOfDay());
		validate("the beginning of the past week", context3, t3.minusWeeks(1).withDayOfWeek(1).withTimeAtStartOfDay());
		validate("the beginning of next year", context3, t3.plusYears(1).year().roundFloorCopy());
		validate("the beginning of next friday", context3, t3.withDayOfWeek(5).withTimeAtStartOfDay());
		validate("the beginning of next monday", context3, t3.plusWeeks(1).withDayOfWeek(1).withTimeAtStartOfDay());
		//mixed
		validate("3 days before the end of next month", context3, t3.plusMonths(1).dayOfMonth()
				.withMaximumValue().withTimeAtStartOfDay().minusDays(3));
	}

	@Test
	public void testFloorCeilingAbsolutes() {
		//floor or ceiling of an absolute (ceiling is the day truncated and with its first child set to the highest value)
		//so end of monday is monday at 11pm, we might have to change this for date ranges, it seems silly however
		//to have end of monday at 11:59:59.999...there is no right answer to this
		validate("the end of today", context3, t3.dayOfWeek().roundFloorCopy().withHourOfDay(23));
		validate("the end of tomorrow", context3, t3.plusDays(1).dayOfWeek().roundFloorCopy().withHourOfDay(23));
		validate("the beginning of yesterday", context3, t3.plusDays(-1).dayOfWeek().roundFloorCopy());
	}

	@Test
	public void testDaysAsTimeunitsRelative() {
		//incrementing repeating timeunits with relative starting dates
		validate("3 fridays from now", context3, t3.plusWeeks(2).withDayOfWeek(5).withTimeAtStartOfDay());
		validate("2 junes from now", context3, t3.plusYears(2).monthOfYear().roundFloorCopy());
		validate("2 junes after last month", context3, t3.plusYears(1).monthOfYear().roundFloorCopy());
		validate("the following friday", context3, t3.withDayOfWeek(5).withTimeAtStartOfDay());
		validate("the thursday after the next friday", context3, t3.withDayOfWeek(4).plusWeeks(1).withTimeAtStartOfDay());
		validate("3 thursdays after next month", context3, t3.plusMonths(1).plusDays(6).withTimeAtStartOfDay());
		validate("3 thursdays after a month from now", context3, t3.plusMonths(1).withDayOfMonth(31).withTimeAtStartOfDay());
		validate("3 thursdays before last friday", context3, t3.minusWeeks(3).withDayOfWeek(4).dayOfWeek().roundFloorCopy());
	}

	@Test
	public void testNamesAsTimeunits() {
		//incrementing repeating timeunits
		validate("2 wednesdays later", context3, t3.plusWeeks(2).withDayOfWeek(3).dayOfWeek().roundFloorCopy());
		validate("2 thursdays later", context3, t3.plusWeeks(1).withDayOfWeek(4).dayOfWeek().roundFloorCopy());
		validate("3 fridays in the future", context3, t3.plusWeeks(2).withDayOfWeek(5).dayOfWeek().roundFloorCopy());
		validate("2 junes ago", context3, t3.minusYears(2).monthOfYear().roundFloorCopy());
		validate("a saturday in the past", context3, t3.minusWeeks(1).withDayOfWeek(6).dayOfWeek().roundFloorCopy());
		validate("the saturday in the past", context3, t3.minusWeeks(1).withDayOfWeek(6).dayOfWeek().roundFloorCopy());
	}

	@Test
	public void testNamedTimeunitIncrements() {
		//incrementing repeating timeunit designations ('monday' or 'june' instead of 'week' or 'month')
		validate("next april", context3, t3.plusYears(1).withMonthOfYear(4).monthOfYear().roundFloorCopy());
		validate("next monday", context3, t3.plusWeeks(1).withDayOfWeek(1).withTimeAtStartOfDay());
		validate("this tuesday", context3, t3.withDayOfWeek(2).withTimeAtStartOfDay());
		validate("this monday", context3, t3.withDayOfWeek(1).withTimeAtStartOfDay());
		validate("last monday", context3, t3.withDayOfWeek(1).withTimeAtStartOfDay()); //should be previous week ? :\
		validate("next monday", context3, t3.plusWeeks(1).withDayOfWeek(1).withTimeAtStartOfDay());
		validate("this friday", context3, t3.plusDays(2).withTimeAtStartOfDay());
		validate("next friday", context3, t3.plusDays(2).withTimeAtStartOfDay());
		validate("last tuesday", context3, t3.minusDays(1).withTimeAtStartOfDay());
		validate("last june", context3, t3.minusYears(1).withMonthOfYear(6).monthOfYear().roundFloorCopy());
		validate("past june", context3, t3.minusYears(1).withMonthOfYear(6).monthOfYear().roundFloorCopy());
		validate("this saturday", context3, t3.withDayOfWeek(6).withTimeAtStartOfDay());
		validate("this past june", context3, t3.minusYears(1).withMonthOfYear(6).monthOfYear().roundFloorCopy());
		validate("this past saturday", context3, t3.minusWeeks(1).withDayOfWeek(6).withTimeAtStartOfDay());
		validate("the past saturday", context3, t3.minusWeeks(1).withDayOfWeek(6).withTimeAtStartOfDay());
	}

	@Test
	public void testRelativeStartingDates() {
		//mixed increments from relative starting dates
		validate("25 minutes from now", context3, t3.plusMinutes(25));
		validate("4 hours prior to tomorrow", context3, t3.plusDays(1).dayOfMonth().roundFloorCopy().minusHours(4));
		validate("25 days from yesterday", context3, t3.minusDays(1).plusDays(25).withTimeAtStartOfDay());
		validate("a week from tomorrow", context3, t3.plusDays(1).plusWeeks(1).withTimeAtStartOfDay());

		validate("2 weeks after 3 months after 5 weeks before 2 years from 2 days ago", context3,
				t3.minusDays(2).plusYears(2).minusWeeks(5).plusMonths(3).plusWeeks(2));

		validate("2 weeks from now", context3, t3.plusWeeks(2));
		validate("the day after tomorrow", context3, t3.plusDays(2).withTimeAtStartOfDay());
		validate("2 minutes from now", context3, t3.plusMinutes(2));
		validate("3 fortnights from now", context3, t3.plusWeeks(6));
		validate("the minute after this minute", context3, t3.plusMinutes(1).minuteOfHour().roundFloorCopy());

		validate("a minute after the current minute", context3, t3.plusMinutes(1).minuteOfHour().roundFloorCopy());
		validate("the minute after the minute after the next minute", context3,t3.plusMinutes(3).minuteOfHour().roundFloorCopy());
		validate("3 minutes after the previous day", context3, t3.minusDays(1).withTimeAtStartOfDay().plusMinutes(3).minuteOfHour().roundFloorCopy());
		validate("5 minutes before 2 hours from 5 minutes ago", context3, t3.minusMinutes(5).plusHours(2).minusMinutes(5));
	}

	@Test
	public void testNumberBasedIncrements() {
		//number-based increments
		validate("a week ago", context3, t3.minusWeeks(1));
		validate("3 months ago", context3, t3.minusMonths(3));
		validate("5 years ago", context3, t3.minusYears(5));
		validate("25 weeks in the future", context3, t3.plusWeeks(25));
		validate("12 days later", context3, t3.plusDays(12));
		validate("an hour later", context3, t3.plusHours(1));
		validate("the following day", context3, t3.plusDays(1).withTimeAtStartOfDay());
	}

	@Test
	public void testIncrementedTimeunits() {
		//increments like next & last, child values get truncated, e.g. "next week" will be Monday @ 00:00:00
		validate("NEXT weEk", context3, t3.plusWeeks(1).withDayOfWeek(1).dayOfWeek().roundFloorCopy());
		validate("last month", context3, t3.minusMonths(1).monthOfYear().roundFloorCopy());
		validate("next year", context3, t3.plusYears(1).year().roundFloorCopy());
		validate("next day", context3, t3.plusDays(1).withTimeAtStartOfDay());
		validate("this day", context3, t3.withTimeAtStartOfDay());
	}

	@Test
	public void testInvalidValues() {
		validate("next quarter", null, null); //for now?
		validate("notadate", null, null);
		validate("last night", null, null);
		validate("the middle of last week", null, null);
		validate("next", null, null);
		validate("next tomorrow", null, null);
		validate("2 yesterdays ago", null, null);
		validate("the year before last next week", null, null);
	}

	@Test
	public void testDifferingContexts(){//all kinds
		for (int i = 0; i < ts.length; i++) {
			validate("tomorrow", contexts[i], ts[i].plusDays(1).withTimeAtStartOfDay());
			validate("the end of last year", contexts[i], ts[i].minusYears(1).year().roundFloorCopy().dayOfYear().withMaximumValue());
			validate("next week on tuesday", contexts[i], ts[i].plusWeeks(1).withDayOfWeek(2).dayOfWeek().roundFloorCopy());
			validate("a year from now at noon", contexts[i], ts[i].plusYears(1).withTimeAtStartOfDay().withHourOfDay(12));
			validate("2 days after the beginning of next month", contexts[i], ts[i].plusMonths(1).monthOfYear().roundFloorCopy().plusDays(2));
			validate("the end of the week", contexts[i], ts[i].withDayOfWeek(7).withTimeAtStartOfDay());
		}
	}

	@Test
	@Ignore
	public void testNaturalLanguageDatesWithTimezones(){
		//TODO add some nat language tests with timezones
	}
}
