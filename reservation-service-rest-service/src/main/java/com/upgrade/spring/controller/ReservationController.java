package com.upgrade.spring.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.upgrade.spring.dto.ReservationConfirmationDto;
import com.upgrade.spring.dto.ReservationInfoDto;
import com.upgrade.spring.exception.ReservationException;
import com.upgrade.spring.model.Reservation;
import com.upgrade.spring.repository.ReservationRepository;
import com.upgrade.spring.controller.utils.ReservationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin( origins = "http://localhost:8081" )
@RestController
@RequestMapping( "" )
public class ReservationController
{
	@Autowired
	ReservationRepository reservationRepository;

	@GetMapping( "/reservations" )
	public ResponseEntity<List<LocalDate>> getAvailableDates()
	{
		try
		{
			LocalDate startDate = ReservationUtils.calculateStartDate();

			LocalDate endDate = startDate.plusDays( 30 );

			// create list of 30 dates
			List<LocalDate> fullListOfDates = ReservationUtils.getListOfDaysBetweenDates( startDate, endDate );

			// find all reserved within 30 days
			List<Reservation> reservations = reservationRepository.findAllByReservationDateBetween( startDate, endDate );

			// create list of reserved dates
			List<LocalDate> datesReserved = ReservationUtils.getBookedDatesFromReservations( reservations );

			// remove reserved dates from final list
			List<LocalDate> availableDates = ReservationUtils.purgeReservedDates( fullListOfDates, datesReserved );

			return new ResponseEntity<List<LocalDate>>( availableDates, HttpStatus.OK );
		}
		catch ( Exception e )
		{
			return new ResponseEntity<>( null, HttpStatus.INTERNAL_SERVER_ERROR );
		}
	}

	@GetMapping( "/reservations/{id}" )
	public ResponseEntity<ReservationInfoDto> getReservationById( @PathVariable( "id" ) String reservationId )
	{
		List<Reservation> reservationData = reservationRepository.findByReservationId( reservationId );

		if ( !reservationData.isEmpty() )
		{
			String firstName = reservationData.get( 0 ).getFirstName();
			String lastName = reservationData.get( 0 ).getLastName();
			String email = reservationData.get( 0 ).getEmail();

			// check-in date
			LocalDate minDate = reservationData.stream()
					.filter( reservation -> reservation.getReservationDate() != null )
					.map( Reservation::getReservationDate ).min( LocalDate::compareTo )
					.get();

			// check-out date
			LocalDate maxDate = reservationData.stream()
					.filter( reservation -> reservation.getReservationDate() != null )
					.map( Reservation::getReservationDate ).max( LocalDate::compareTo )
					.get().plusDays( 1 );

			return new ResponseEntity<ReservationInfoDto>( new ReservationInfoDto( firstName, lastName, email, minDate, maxDate ), HttpStatus.OK );
		}
		else
		{
			return new ResponseEntity<>( HttpStatus.NOT_FOUND );
		}
	}

	@PostMapping( "/reservations" )
	public ResponseEntity<ReservationConfirmationDto> createReservation( @RequestBody ReservationInfoDto reservationInfo )
	{

		// verify no previous booking within the 30 day timeframe
		if ( verifyNoPreviousBooking( reservationInfo.getEmail() ) )
		{
			throw new ReservationException( HttpStatus.INTERNAL_SERVER_ERROR, "There is a previous booking within the 30 days period." );
		}

		// verify  dates are available for booking
		if ( reservationRepository.findAllByReservationDateBetween( reservationInfo.getCheckInDate(),
				reservationInfo.getCheckOutDate().minusDays( 1 ) ).size() > 0 )
		{
			throw new ReservationException( HttpStatus.INTERNAL_SERVER_ERROR, "One or more dates are no longer available for reservation." );
		}

		// verify reservation date falls within the limits
		verifyReservationDates( reservationInfo );

		LocalDate checkin = reservationInfo.getCheckInDate();
		LocalDate checkout = reservationInfo.getCheckOutDate();

		// create unique id for this reservation
		String uuid = UUID.randomUUID().toString();

		while ( checkin.isBefore( checkout ) || checkin.equals( checkout.minusDays( 1 ) ) )
		{
			reservationRepository
					.save( new Reservation( uuid, reservationInfo.getFirstName(),
							reservationInfo.getLastName(), reservationInfo.getEmail(), checkin ) );

			checkin = checkin.plusDays( 1 );
		}

		return new ResponseEntity<>( new ReservationConfirmationDto( uuid ), HttpStatus.CREATED );

	}

	@PutMapping( "/reservations/{id}" )
	public ResponseEntity<HttpStatus> updateReservation( @PathVariable( "id" ) String reservationId, @RequestBody ReservationInfoDto reservationInfo )
	{
		// verify reservation date falls within the limits
		verifyReservationDates( reservationInfo );

		// find reservation
		List<Reservation> reservationData = reservationRepository.findByReservationId( reservationId );

		if ( reservationData.isEmpty() )
		{
			return new ResponseEntity<>( HttpStatus.NOT_FOUND );
		}

		// Check if the dates changes are available
		List<Reservation> bookedDates = reservationRepository.findAllByReservationBetweenDatesAndNotSameReservationId( reservationInfo
				.getCheckInDate(), reservationInfo.getCheckOutDate(), reservationId );

		if ( bookedDates.size() > 0 )
		{
			throw new ReservationException( HttpStatus.INTERNAL_SERVER_ERROR, "One or more dates are no longer available for reservation." );
		}
		else
		{
			// remove previous booking and create new booking
			reservationRepository.removeByReservationId( reservationId );

			LocalDate checkin = reservationInfo.getCheckInDate();
			LocalDate checkout = reservationInfo.getCheckOutDate();

			while ( checkin.isBefore( checkout ) || checkin.equals( checkout.minusDays( 1 ) ) )
			{
				reservationRepository
						.save( new Reservation( reservationId, reservationInfo.getFirstName(),
								reservationInfo.getLastName(), reservationInfo.getEmail(), checkin ) );

				checkin = checkin.plusDays( 1 );
			}

			return new ResponseEntity<>( HttpStatus.NO_CONTENT );
		}
	}

	@DeleteMapping( "/reservations/{id}" )
	public ResponseEntity<HttpStatus> cancelReservation( @PathVariable( "id" ) String reservationId )
	{
		try
		{
			reservationRepository.removeByReservationId( reservationId );
			return new ResponseEntity<>( HttpStatus.NO_CONTENT );
		}
		catch ( Exception e )
		{
			return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
		}
	}

	private void verifyReservationDates( ReservationInfoDto reservationInfo )
	{
		LocalDate checkin = reservationInfo.getCheckInDate();
		LocalDate checkout = reservationInfo.getCheckOutDate();

		// verify reservation does not exceed 3 days; checkin and checkout dates are correct
		if ( ReservationUtils.calculateDifferenceBetweenDates( checkin, checkout ) > 3 )
		{
			throw new ReservationException( HttpStatus.INTERNAL_SERVER_ERROR, "Reservation can not exceed 3 days." );
		}
		else if ( ReservationUtils.calculateDifferenceBetweenDates( checkin, checkout ) < 0 )
		{
			throw new ReservationException( HttpStatus.INTERNAL_SERVER_ERROR, "Check-in date is greater than Check-out date." );
		}
		else if ( ReservationUtils.calculateDifferenceBetweenDates( checkin, checkout ) == 0 )
		{
			throw new ReservationException( HttpStatus.INTERNAL_SERVER_ERROR, "Check-in and Check-out dates are the same." );
		}

		LocalDate startDate = ReservationUtils.calculateStartDate();
		LocalDate endDate = startDate.plusDays( 30 );

		// verify reservation is with 30 days
		if ( reservationInfo.getCheckInDate().isBefore( startDate ) || reservationInfo.getCheckOutDate().isAfter( endDate ) )
		{
			throw new ReservationException( HttpStatus.INTERNAL_SERVER_ERROR, "Reservation outside the allowable date range." );
		}
	}

	private boolean verifyNoPreviousBooking( String email )
	{
		LocalDate startDate = ReservationUtils.calculateStartDate();
		LocalDate endDate = startDate.plusDays( 30 );

		List<Reservation> reservations = reservationRepository.findAllByReservationDateBetweenAndEmail( startDate, endDate, email );

		if ( reservations.size() > 0 )
		{
			return true;
		}
		else
		{
			return false;
		}

	}

}
