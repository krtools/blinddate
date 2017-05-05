package com.github.krtools.blinddate;

final class NumberTokenOps {

	static enum NumberToken {
		ONE(1), //
		TWO(2), //
		THREE(3), //
		FOUR(4), //
		FIVE(5), //
		SIX(6), //
		SEVEN(7), //
		EIGHT(8), //
		NINE(9), //
		
		FIRST(1), //
		SECOND(2), //
		THIRD(3), //
		FOURTH(4), //
		FIFTH(5), //
		SIXTH(6), //
		SEVENTH(7), //
		EIGHTH(8), //
		NINTH(9), //

		TEN(10), //
		ELEVEN(11), //
		TWELVE(12), //
		THIRTEEN(13), //
		FOURTEEN(14), //
		FIFTEEN(15), //
		SIXTEEN(16), //
		SEVENTEEN(17), //
		EIGHTEEN(18), //
		NINETEEN(19), //

		TWENTY(20), //
		THIRTY(30), //
		FOURTY(40), //
		FIFTY(50), //
		SIXTY(60), //
		SEVENTY(70), //
		EIGHTY(80), //
		NINETY(90), //

		TENTH(10), //
		ELEVENTH(11), //
		TWELFTH(12), //
		THIRTEENTH(13), //
		FOURTEENTH(14), //
		FIFTEENTH(15), //
		SIXTEENTH(16), //
		SEVENTEENTH(17), //
		EIGHTEENTH(18), //
		NINETEENTH(19), //
		TWENTIETH(20), //
		THIRTIETH(30), //
		FOURTIETH(40), //
		FIFTIETH(50), //
		SIXTIETH(60), //
		SEVENTIETH(70), //
		EIGHTIETH(80), //
		NINETIETH(90);

		private final int value;

		private NumberToken(int v) {
			value = v;
		}

	}

	public static int getNumber(final String input) {
		final String value = input.toUpperCase();
		if (value == null) {
			return -1;
		} else {
			if (value.contains("-") && value.indexOf('-') == value.lastIndexOf('-')) {
				String[] numParts = value.split("-");
				int total = 0;
				for (String numPart : numParts) {
					total += NumberToken.valueOf(numPart).value;
				}
				return total;
			}
			return NumberToken.valueOf(value).value;
		}
	}
}
