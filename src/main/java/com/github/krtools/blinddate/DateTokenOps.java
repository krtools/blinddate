package com.github.krtools.blinddate;

import org.joda.time.DateTime;

final class DateTokenOps {

	static enum AbsoluteToken {
		NOW, TODAY, YESTERDAY, TOMORROW
	}

	static enum IncrementToken {
		NEXT, LAST, PREVIOUS, COMING, THIS, CURRENT, FOLLOWING, PRECEDING, PRESENT, PAST, THIS_PAST, THE_PAST
	}

	static enum TimeUnitToken {
		SECOND, MINUTE, HOUR, DAY, WEEK, FORTNIGHT, MONTH, QUARTER, YEAR,
	}

	static enum RelativeSpanToken {
		FROM, AFTER, BEFORE, BEYOND, PRIOR_TO
	}

	static enum SpanDirectionToken {
		AGO, IN_THE_PAST, IN_THE_FUTURE, LATER
	}
	
	static enum TimeToken{
		NOON, MIDNIGHT
	}

	static enum MonthToken {
		JANUARY(1), FEBRUARY(2), MARCH(3), APRIL(4), MAY(5), JUNE(6), JULY(7), AUGUST(8), SEPTEMBER(9), OCTOBER(10), NOVEMBER(11), DECEMBER(12);
		private final int value;
		private MonthToken(int v) {
			value = v;
		}
		public int getValue() {
			return value;
		}
	}

	static enum WeekDayToken {
		MONDAY(1), TUESDAY(2), WEDNESDAY(3), THURSDAY(4), FRIDAY(5), SATURDAY(6), SUNDAY(7);
		private final int value;
		private WeekDayToken(int v) {
			value = v;
		}
		public int getValue() {
			return value;
		}
	}
	
	static int getMonthInt(final String month){
		return MonthToken.valueOf(month).value;
	}
	
	static int getWeekDayInt(final String weekday){
		return WeekDayToken.valueOf(weekday).value;
	}

	static DateTime getAbsolute(final String dateRef, final DateTime dt) {
		switch (AbsoluteToken.valueOf(dateRef)) {
		case NOW:
			return dt;
		case TODAY:
			return dt.withTimeAtStartOfDay();
		case TOMORROW:
			return dt.plusDays(1).withTimeAtStartOfDay();
		case YESTERDAY:
			return dt.minusDays(1).withTimeAtStartOfDay();
		default:
			throw new IllegalStateException(tokenISE("absolutedate", dateRef));
		}
	}
	
	static DateTime getAbsoluteTime(final DateTime dt, final String time, final boolean forward){
		switch(TimeToken.valueOf(time)){
		case MIDNIGHT:			
			if(forward){
				return dt.millisOfDay().get() == 0 ? dt : dt.plusDays(1).withTimeAtStartOfDay();
			} else {
				return dt.withTimeAtStartOfDay();
			}			
		case NOON:
			final DateTime noonVal = dt.withHourOfDay(12).hourOfDay().roundFloorCopy();
			if(forward){				
				return dt.compareTo(noonVal) > 0 ? noonVal.plusDays(1) : noonVal;
			} else {
				return noonVal;
			}
		default:
			throw new IllegalArgumentException(tokenISE("absolutetime", time));
		}
	}

	static DateTime getIncremented(final int incrementVal,
			final String timeunit, final DateTime context,
			final boolean truncate) {
		return getIncremented(incrementVal,
				TimeUnitToken.valueOf(timeunit.replaceAll("S$", "")), context,
				truncate);
	}

	private static DateTime getIncremented(final int incrementVal,
			final TimeUnitToken timeunit, final DateTime context,
			final boolean truncate) {
		switch (timeunit) {
		case DAY:
			return context.plusDays(incrementVal);
		case HOUR:
			return context.plusHours(incrementVal);
		case MINUTE:
			return context.plusMinutes(incrementVal);
		case MONTH:
			return context.plusMonths(incrementVal);
		case FORTNIGHT:
			return context.plusWeeks(incrementVal * 2);
			// case QUARTER:
			// return null;
		case SECOND:
			return context.plusSeconds(incrementVal);
		case WEEK:
			return context.plusWeeks(incrementVal);
		case YEAR:
			return context.plusYears(incrementVal);
			// truncate day of year or day of month??
		default:
			throw new IllegalStateException(tokenISE("timeunit", timeunit.toString()));
		}
	}

	static int getRelativeSpan(final String span) {
		switch (RelativeSpanToken.valueOf(span.replace(' ', '_'))) {
		case AFTER:
		case FROM:
		case BEYOND:
			return 1;
		case BEFORE:
		case PRIOR_TO:
			return -1;
		default:
			throw new IllegalStateException(tokenISE("relative span", span));
		}
	}

	static int getSpanDirection(final String spanDir) {
		switch (SpanDirectionToken.valueOf(spanDir.replace(' ', '_'))) {
		case AGO:
		case IN_THE_PAST:
			return -1;
		case LATER:
		case IN_THE_FUTURE:
			return 1;
		default:
			throw new IllegalStateException(tokenISE("span direction", spanDir));
		}
	}

	static int getIncrementVal(final String increment) {
		switch (IncrementToken.valueOf(increment.replace(' ', '_'))) {
		case NEXT:
		case COMING:
		case FOLLOWING:
			return 1;
		case LAST:
		case PREVIOUS:
		case PRECEDING:
		case PAST:
		case THIS_PAST:
		case THE_PAST:
			return -1;
		case THIS:
		case CURRENT:
		case PRESENT:
			return 0;
		default:
			throw new IllegalStateException(tokenISE("increment", increment));
		}
	}

	static DateTime incrementWeekDay(final DateTime dt, final int increment,
			final String weekDayText) {
		final int weekDay = WeekDayToken.valueOf(
				weekDayText.replaceAll("S$", "")).getValue();
		final int incValue = rollIncValue(increment, dt.getDayOfWeek(), weekDay);
		return dt.plusWeeks(incValue).withDayOfWeek(weekDay).dayOfWeek()
				.roundFloorCopy();
	}

	static DateTime incrementMonth(final DateTime dt, final int increment,
			final String monthText) {
		final int month = MonthToken.valueOf(monthText.replaceAll("S$", ""))
				.getValue();
		final int incValue = rollIncValue(increment, dt.getMonthOfYear(), month);
		return dt.plusYears(incValue).withMonthOfYear(month).monthOfYear()
				.roundFloorCopy();
	}

	static DateTime truncateMonth(final DateTime dt, boolean floor) {
		return truncate(dt, TimeUnitToken.MONTH, floor);
	}

	static DateTime truncateWeekDay(final DateTime dt, boolean floor) {
		return truncate(dt, TimeUnitToken.DAY, floor);
	}

	static DateTime truncate(final DateTime dt, final String timeunit,
			final boolean floor) {
		return truncate(dt, TimeUnitToken.valueOf(timeunit.replaceAll("S$", "")), floor);
	}

	static String tokenISE(String tokenType, String input) {
		return "text did not match any valid tokens of type " + tokenType
				+ " for input: " + input;
	}
	
	private static DateTime truncate(final DateTime dt, TimeUnitToken timeunit,
			final boolean floor) {
		final DateTime retVal;
		switch (timeunit) {
		case DAY:
			retVal = dt.dayOfMonth().roundFloorCopy();
			return floor ? retVal : retVal.withHourOfDay(23);
		case FORTNIGHT:
			return dt;
		case HOUR:
			retVal = dt.hourOfDay().roundFloorCopy();
			return floor ? retVal : retVal.withMinuteOfHour(59);
		case MINUTE:
			retVal = dt.minuteOfHour().roundFloorCopy();
			return floor ? retVal : retVal.withSecondOfMinute(59);
		case MONTH:
			retVal = dt.monthOfYear().roundFloorCopy();
			return floor ? retVal : retVal.dayOfMonth().withMaximumValue();
			// case QUARTER:
			// return null;
		case SECOND:
			retVal = dt.secondOfMinute().roundFloorCopy();
			return floor ? retVal : retVal.millisOfSecond().withMaximumValue();
		case WEEK:
			retVal = dt.weekOfWeekyear().roundFloorCopy();
			return floor ? retVal : retVal.dayOfWeek().withMaximumValue();
		case YEAR:
			retVal = dt.year().roundFloorCopy();
			return floor ? retVal : retVal.dayOfYear().withMaximumValue();
		default:
			throw new IllegalStateException(tokenISE("timeunit", timeunit.toString()));
		}
	}
	
	/*
	 * when going forward and incrementing, if currval < targetval, we increment
	 * 1 less since incrementing once will count as a pass "through" targetVal
	 * when we roll over to the next increment.
	 * 
	 * same for incrementing backward, but opposite comparison.
	 */
	private static int rollIncValue(int inc, int currVal, int targetVal) {
		if (inc > 0) {
			return currVal < targetVal ? inc - 1 : inc;
		} else if (inc < 0) {
			return currVal > targetVal ? inc + 1 : inc;
		} else {
			return inc;
		}
	}
}