grammar Date;


date:
 		absolutedate (AT unit=TIME_ABSOLUTE)?  										#absoluteDateText
	|	timespan (dir=SPAN_DIR | (dir=REL_SPAN date)) 								#dateModifier
	|	incrementedDate (ON onday=WEEKDAY)?											#incrementalDate
	|	THE? end=(START|END) OF THE?
		(timeunit | incrementedDate | absolutedate)									#startOrEndOfDate
	|	THE? unit=(WEEKDAY | MONTH) OF incrementedDate								#incrementalDateWithName
;

incrementedDate: THE? INCREMENT timeunit
;

//30 days, 20 minutes, etc...
timespan: coeff=(NUMBER | THE) timeunit
;

//week, day, month, year, friday, june, etc..
timeunit: unit=(TIMEUNIT | WEEKDAY | MONTH)?
;
//today, tomorrow, etc...
absolutedate: ABSOLUTE
;

NUMBER: 		[0-9]+;
TIMEUNIT: 		('SECOND'|'MINUTE'|'HOUR'|'DAY'|'WEEK'|'FORTNIGHT'|'MONTH'|'QUARTER'|'YEAR') 'S'?;
SPAN_DIR: 		'AGO'|'IN THE PAST'|'IN THE FUTURE'|'LATER';
REL_SPAN: 		'FROM'|'AFTER'|'BEFORE'|'PRIOR TO';
ABSOLUTE: 		'NOW'|'TODAY'|'YESTERDAY'|'TOMORROW';
TIME_ABSOLUTE: 	'NOON'|'MIDNIGHT';
INCREMENT: 		'NEXT'|'LAST'|'PREVIOUS'|'PRECEDING'|'FOLLOWING'|'COMING'|'CURRENT'|'PRESENT'|'PAST'|('TH') ('E'|'IS') ' PAST'|'THIS';
WEEKDAY:		('MONDAY'|'TUESDAY'|'WEDNESDAY'|'THURSDAY'|'FRIDAY'|'SATURDAY'|'SUNDAY') 'S'?;
MONTH: 			('JANUARY'|'FEBRUARY'|'MARCH'|'APRIL'|'MAY'|'JUNE'|'JULY'|'AUGUST'|'SEPTEMBER'|'OCTOBER'|'NOVEMBER'|'DECEMBER') 'S'?;
START: 			'BEGINNING'|'START';
END:			'END';
//ANDAHALF: 	'AND A HALF';
//NUMBER_TXT: 	[0-9]+ ('TH'|'ND'|'RD'|'ST'); //not perfect but good enough (small and flexible a plus?)
OF:				'OF';
ON:				'ON';
AT:				'AT';
THE:	   		'THE'|'A' 'N'?;
WS:   			[ \t\r\n]+ -> skip;