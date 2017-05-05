package com.github.krtools.blinddate;

import static com.github.krtools.blinddate.DateTokenOps.getAbsolute;
import static com.github.krtools.blinddate.DateTokenOps.getAbsoluteTime;
import static com.github.krtools.blinddate.DateTokenOps.getIncrementVal;
import static com.github.krtools.blinddate.DateTokenOps.getIncremented;
import static com.github.krtools.blinddate.DateTokenOps.getMonthInt;
import static com.github.krtools.blinddate.DateTokenOps.getRelativeSpan;
import static com.github.krtools.blinddate.DateTokenOps.getSpanDirection;
import static com.github.krtools.blinddate.DateTokenOps.getWeekDayInt;
import static com.github.krtools.blinddate.DateTokenOps.incrementMonth;
import static com.github.krtools.blinddate.DateTokenOps.incrementWeekDay;
import static com.github.krtools.blinddate.DateTokenOps.tokenISE;
import static com.github.krtools.blinddate.DateTokenOps.truncate;
import static com.github.krtools.blinddate.DateTokenOps.truncateMonth;
import static com.github.krtools.blinddate.DateTokenOps.truncateWeekDay;

import org.antlr.v4.runtime.Token;
import org.joda.time.DateTime;

import com.github.krtools.blinddate.antlr4.DateBaseVisitor;
import com.github.krtools.blinddate.antlr4.DateParser;
import com.github.krtools.blinddate.antlr4.DateParser.AbsoluteDateTextContext;
import com.github.krtools.blinddate.antlr4.DateParser.DateModifierContext;
import com.github.krtools.blinddate.antlr4.DateParser.IncrementalDateContext;
import com.github.krtools.blinddate.antlr4.DateParser.IncrementalDateWithNameContext;
import com.github.krtools.blinddate.antlr4.DateParser.IncrementedDateContext;
import com.github.krtools.blinddate.antlr4.DateParser.StartOrEndOfDateContext;
import com.github.krtools.blinddate.antlr4.DateParser.TimespanContext;
import com.github.krtools.blinddate.antlr4.DateParser.TimeunitContext;

public final class DateVisitorImpl extends DateBaseVisitor<DateTime> {

	private final DateTime dateTimeContext;

	public DateVisitorImpl(DateTime context) {
		this.dateTimeContext = context;
	}

	@Override
	public DateTime visitAbsoluteDateText(AbsoluteDateTextContext ctx) {
		final String dateRef = ctx.absolutedate().ABSOLUTE().getText();
		final String absoluteTime = ctx.unit != null ? ctx.unit.getText() : null;
		if(ctx.unit == null){
			return getAbsolute(dateRef, dateTimeContext);
		}else {
			return getAbsoluteTime(getAbsolute(dateRef, dateTimeContext), absoluteTime, false);
		}
	}

	@Override
	public DateTime visitIncrementedDate(IncrementedDateContext ctx) {
		final String increment = ctx.INCREMENT().getText();
		final int incrementVal = getIncrementVal(increment);
		final TimeunitContext timeunit;
		if(ctx.timeunit().unit != null){
			timeunit = ctx.timeunit();
		}else { //handles the backreference e.g. "the monday before last <MISSING TIMEUNIT>" (hacky)
			if(ctx.parent.parent instanceof DateModifierContext){//antlr solution would be preferred
				timeunit = ((DateModifierContext)ctx.parent.parent).timespan().timeunit();
			} else {
				return null;
			}
		}

		switch (timeunit.unit.getType()) {
		case DateParser.TIMEUNIT:
			return truncate(getIncremented(incrementVal, timeunit.TIMEUNIT().getText(),
					dateTimeContext, true), timeunit.TIMEUNIT().getText(), true);
		case DateParser.MONTH:
			return incrementMonth(dateTimeContext, getIncrementVal(increment),
					timeunit.MONTH().getText());
		case DateParser.WEEKDAY:
			return incrementWeekDay(dateTimeContext,
					getIncrementVal(increment), timeunit.WEEKDAY().getText());
		default:
			throw new IllegalStateException(tokenISE("date unit",
					timeunit.unit.getText()));
		}
	}

	@Override
	public DateTime visitDateModifier(DateModifierContext ctx) {
		final int scale = ctx.timespan().THE() == null ? Integer.parseInt(ctx
				.timespan().NUMBER().getText()) : 1;

		final int dir = ctx.dir.getType();
		final int spanDir;
		//asking if is non-relative like 'AGO' or 'LATER' or relative like 'AFTER <ANOTHER DATE>'
		if (dir == DateParser.SPAN_DIR) {
			spanDir = getSpanDirection(ctx.SPAN_DIR().getSymbol().getText());
		} else { // is a REL_SPAN
			spanDir = getRelativeSpan(ctx.REL_SPAN().getText());
		}

		if (ctx.timespan().timeunit().TIMEUNIT() != null) {
			return getTimespanByTimeunit(ctx, scale, dir, spanDir);
		} else {
			if(ctx.date() == null){
				return getTimespanByNamedTimeunit(ctx.timespan(),
						dateTimeContext, scale * spanDir);
			}else {
				return getTimespanByNamedTimeunit(ctx.timespan(),
					visit(ctx.date()), scale * spanDir);
			}
		}
	}

	@Override
	//start of...end of...
	public DateTime visitStartOrEndOfDate(StartOrEndOfDateContext ctx) {
		final boolean isStart = ctx.end.getType() == DateParser.START;
		final DateTime dt;
		final int unitType;
		final String timeunit;
		if(ctx.incrementedDate() != null){//...next week/month/year
			unitType = ctx.incrementedDate().timeunit().unit.getType();
			dt = visit(ctx.incrementedDate());
			timeunit = ctx.incrementedDate().timeunit().getText();
		} else if (ctx.timeunit() != null){ //just a timeunit! 'the month'
			unitType = ctx.timeunit().unit.getType();
			timeunit = ctx.timeunit().getText();
			dt = dateTimeContext;
		} else {//today, tomorrow, yesterday
			dt = getAbsolute(ctx.absolutedate().getText(), dateTimeContext);
			return isStart ? dt.withTimeAtStartOfDay() : dt.dayOfWeek().roundFloorCopy().withHourOfDay(23);
		}
		switch(unitType){//handle the 'beginning' or 'end' part
		case DateParser.TIMEUNIT:
			return truncate(dt, timeunit, isStart);
		case DateParser.WEEKDAY:
			return truncateWeekDay(dt.withDayOfWeek(getWeekDayInt(timeunit)), isStart);
		case DateParser.MONTH:
			return truncateMonth(dt.withMonthOfYear(getMonthInt(timeunit)), isStart);
		default:
			throw new IllegalStateException(tokenISE("timeunit", timeunit));
		}
	}

	@Override
	public DateTime visitIncrementalDate(IncrementalDateContext ctx) {
		//if we have a weekday specification at the end "next week ON FRIDAY"
		if(ctx.onday == null){
			//FIXME: changing this to visit incremented date erroneously returns a date with 'the year before last next week'
			return super.visitIncrementalDate(ctx);
		} else {
			return visit(ctx.incrementedDate()).
					withDayOfWeek(getWeekDayInt(ctx.onday.getText()));
		}
	}

	@Override
	public DateTime visitIncrementalDateWithName(
			IncrementalDateWithNameContext ctx) {
		final DateTime innerDt = visit(ctx.incrementedDate());
		final Token innerUnit = ctx.incrementedDate().timeunit().unit;
		if(innerUnit != null && nameIsWithinTimeunit(ctx.unit, innerUnit)){
			if(ctx.unit.getType() == DateParser.MONTH){
				return innerDt.withMonthOfYear(getMonthInt(ctx.unit.getText()));
			} else {
				return innerDt.withDayOfWeek(getWeekDayInt(ctx.unit.getText()));
			}
		} else {
			return null;
		}
	}

	// calculate the timespan based on a unit of time e.g. 4 months
	private DateTime getTimespanByTimeunit(DateModifierContext ctx,
			final int scale, final int dir, final int spanDir) {
		final String timeunit = ctx.timespan().timeunit().TIMEUNIT().getText();
		if (dir == DateParser.SPAN_DIR) {
			return getIncremented(spanDir * scale, timeunit, dateTimeContext,
					false);
		} else { // is a REL_SPAN
			return getIncremented(spanDir * scale, timeunit, visit(ctx.date()),
					false);
		}
	}

	// calculate the timespan based on a month or weekday e.g. 3 fridays
	private DateTime getTimespanByNamedTimeunit(TimespanContext ctx,
			DateTime dt, int incValue) {
		switch (ctx.timeunit().unit.getType()) {
		case DateParser.MONTH:
			return incrementMonth(dt, incValue, ctx.timeunit().MONTH().getText());
		case DateParser.WEEKDAY:
			return incrementWeekDay(dt, incValue, ctx.timeunit().WEEKDAY().getText());
		default:
			throw new IllegalStateException(tokenISE("timespan", ctx.getText()));
		}
	}

	private boolean nameIsWithinTimeunit(Token name, Token timeunit){
		if(name == null || timeunit == null){
			return false;
		}
		if(name.getType() == DateParser.WEEKDAY && timeunit.getText().equals("WEEK")){
			return true;
		} else if (name.getType() == DateParser.MONTH && timeunit.getText().equals("YEAR")){
			return true;
		} else {
			return false;
		}
	}
}