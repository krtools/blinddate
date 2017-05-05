package com.github.krtools.blinddate;

import org.joda.time.DateTime;
import org.junit.Test;

public class TimestampDateParserTest extends AbstractDateParserTest {
	@Test
	public void testParseTimestamps(){
		final DateTime t3 = new DateTime(2014, 06, 17, 6, 13, 20);
		final DateTime t4 = new DateTime(1973,03,03,04,46,40); 
		validate("1403000000", null, t3);		
		validate("1403000000000", null, t3);
		validate("1403000000000000000", null, t3);
		validate(Long.toString(maxInt), null, new DateTime(2038,1,18,22,14,7));
		//lowest possible
		validate("100000000", null, t4);		
		validate("100000000000", null, t4);
		validate("100000000000000000", null, t4);
	}
	
	@Test
	public void testInvalidValues() {		
		//timestamps that are out of range
		validate("99999999", null, null);
		validate("99999999999", null, null);
		validate("99999999999999999", null, null);
		validate(Long.toString(maxInt+1), null, null);		
		validate("3000000000", null, null);
		validate("3000000000", null, null);
		
		//technically in range of a long but is kept consistent with seconds (to scale)
		validate("3000000000000", null, null);
		validate("3000000000000000", null, null);
		validate("3000000000000000000", null, null);
		
		validate("100000000000000000000000000000000", null, null);
		
		validate("34030000", null, null);		
		validate(Long.toString(maxInt+1), null, null);		        
		validate("0", null, null);
		validate("1", null, null);
	}
}
