package com.github.krtools.blinddate;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.joda.time.DateTime;

import com.github.krtools.blinddate.antlr4.DateLexer;
import com.github.krtools.blinddate.antlr4.DateParser;

public class NaturalLanguageDateParser {
	/**
	 * Attempts to parse a date expressed in textual language such as
	 * "today" or "2 weeks ago" and others. Supports English only.
	 * @param input
	 * @param dateTimeContext
	 * @return
	 */
	public static DateTime parse(String input, DateTime dateTimeContext){
		if(input == null || input.trim().equals("")){
			return null;
		}
		try{
			DateParser parser = setupParser(input.toUpperCase());
			DateVisitorImpl visitor = new DateVisitorImpl(dateTimeContext);
			ParseTree tree = parser.date();
			DateTime dt = visitor.visit(tree);
			return dt;
		} catch (Exception e){
			return null;
		}
	}

	private static DateParser setupParser(String input) {
		DateLexer lexer = setupLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		DateParser parser = new DateParser(tokens);
		parser.removeErrorListeners();
		return parser;
	}

	private static DateLexer setupLexer(String input) {
		ANTLRInputStream inputStream = new ANTLRInputStream(input);
		DateLexer lexer = new DateLexer(inputStream);
		lexer.removeErrorListeners();
		return lexer;
	}
}
