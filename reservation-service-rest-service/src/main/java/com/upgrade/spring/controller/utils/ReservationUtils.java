package com.upgrade.spring.controller.utils;

import com.upgrade.spring.model.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * @author peng
 * @since 12/24/21
 */
public final class ReservationUtils
{

	public static long calculateDifferenceBetweenDates( LocalDate startDate, LocalDate endDate )
	{
		return DAYS.between( startDate, endDate );
	}

	public static List<LocalDate> getListOfDaysBetweenDates( LocalDate startDate, LocalDate endDate )
	{
		long numOfDays = DAYS.between( startDate, endDate );

		List<LocalDate> dates = Stream.iterate( startDate, date -> date.plusDays( 1 ) )
				.limit( numOfDays )
				.collect( Collectors.toList() );
		return dates;
	}

	public static List<LocalDate> getBookedDatesFromReservations( List<Reservation> reservations )
	{
		List<LocalDate> bookedDates = new ArrayList<>();

		reservations.forEach( reservation -> {
			bookedDates.add( reservation.getReservationDate() );
		} );

		return bookedDates;
	}

	public static List<LocalDate> purgeReservedDates( List<LocalDate> fullDatesList, List<LocalDate> reservedDates )
	{
		reservedDates.forEach( localDate -> {
			fullDatesList.remove( localDate );
		} );

		return fullDatesList;
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
