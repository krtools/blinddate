package com.github.krtools.blinddate;

import java.util.HashMap;
import java.util.Map;

public class NumberParser {
	private final static String[] singles = { "zero", "one", "two", "three", "four", "five", "six", "seven", "eight",
			"nine" };

	private final static String[] teens = { "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
			"seventeen", "eighteen", "nineteen" };

	private final static String[] tens = { "ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty",
			"ninety" };

	private final static String[] units = { "hundred", "thousand", "million", "billion", "trillion" };

	private static final Map<String, Integer> nums = initNumberMap();

	private static Map<String, Integer> initNumberMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < singles.length; i++) {
			map.put(singles[i], i);
		}
		for (int i = 0; i < teens.length; i++) {
			map.put(teens[i], i + 10);
		}
		for (int i = 0; i < tens.length; i++) {
			map.put(tens[i], (i + 1) * 10);
		}
		map.put(units[0], 100);
		int unitNum = 1;
		for (int i = 1; i < units.length; i++) {
			map.put(units[i], unitNum *= 1000);
		}
		return map;
	};

	public static Integer getNumber(final String input) {
		if (input == null) {
			return null;
		}
		final String val = input.trim().replaceAll(" +", "-").toLowerCase();

		final String[] numbers = val.split("-");
		if (numbers.length == 1) {
			return nums.get(numbers[0]);
		} else {
			int retVal = 0;
			for (final String strNum : numbers) {
				final Integer interm = nums.get(strNum);
				if (interm != null) {
					retVal += interm;
				} else {
					return null;
				}
			}
			return retVal;
		}
	}
}
