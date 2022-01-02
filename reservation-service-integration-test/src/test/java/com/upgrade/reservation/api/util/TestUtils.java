package com.upgrade.reservation.api.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * @author peng
 * @since 12/24/21
 */
public class TestUtils
{

	public static List<String> getListOfDaysBetweenDates( LocalDate startDate, LocalDate endDate )
	{
		long numOfDays = DAYS.between( startDate, endDate );

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd" );

		List<String> dates = Stream.iterate( startDate, date -> date.plusDays( 1 ) )
				.limit( numOfDays )
				.map( localDate -> localDate.format( formatter ) )
				.collect( Collectors.toList() );
		return dates;
	}

	public static LocalDate calculateStartDate()
	{
		LocalDate startDate;

		if ( LocalDateTime.now().getHour() >= 12 )
		{
			startDate = LocalDate.now().plusDays( 2 );
		}
		else
		{
			startDate = LocalDate.now().plusDays( 1 );
		}
		return startDate;
	}

}
