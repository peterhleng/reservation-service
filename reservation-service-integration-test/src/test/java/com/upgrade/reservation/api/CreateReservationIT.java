package com.upgrade.reservation.api;

import com.upgrade.reservation.api.util.TestUtils;
import com.upgrade.reservation.service.restclient.api.ReservationControllerApi;
import com.upgrade.reservation.service.restclient.model.ErrorResponseDto;
import com.upgrade.reservation.service.restclient.model.ReservationConfirmationDto;
import com.upgrade.reservation.service.restclient.model.ReservationErrorUtils;
import com.upgrade.reservation.service.restclient.model.ReservationInfoDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author peng
 * @since 12/24/21
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = {"classpath:test-context.xml"} )
public class CreateReservationIT
{
	private List<String> confirmationIds = new ArrayList<>();

	@Autowired
	private ReservationControllerApi api;

	@After
	public void cleanUpEach()
	{
		// clean up test data
		for ( String confirmationId : confirmationIds )
		{
			api.cancelReservationUsingDELETE( confirmationId );
		}

		confirmationIds.clear();
	}

	@Test
	public void testCreateReservationSuccess() throws Exception
	{
		// Set up
		LocalDate startDate = TestUtils.calculateStartDate();

		ReservationInfoDto reservationInfo = new ReservationInfoDto().firstName( "Peter" ).lastName( "Eng" ).email( "peterhleng@gmail.com" )
				.checkInDate( startDate.plusDays( 3 ) ).checkOutDate( startDate.plusDays( 6 ) );

		// Exercise
		ReservationConfirmationDto response = api.createReservationUsingPOST( reservationInfo );
		confirmationIds.add( response.getReservationId() );

		// Validate
		assertEquals( HttpStatus.CREATED, api.getApiClient().getStatusCode() );
		assertNotNull( "Contact ID is incorrect", response.getReservationId() );

	}

	@Test
	public void testCreateReservationWithPreviousBookingException() throws Exception
	{
		try
		{
			// Set up
			LocalDate startDate = TestUtils.calculateStartDate();

			ReservationInfoDto reservationInfo1 = new ReservationInfoDto().firstName( "Peter" ).lastName( "Eng" ).email( "peterhleng@gmail.com" )
					.checkInDate( startDate.plusDays( 2 ) ).checkOutDate( startDate.plusDays( 5 ) );

			ReservationConfirmationDto response1 = api.createReservationUsingPOST( reservationInfo1 );
			confirmationIds.add( response1.getReservationId() );

			ReservationInfoDto reservationInfo2 = new ReservationInfoDto().firstName( "Peter" ).lastName( "Eng" ).email( "peterhleng@gmail.com" )
					.checkInDate( startDate.plusDays( 6 ) ).checkOutDate( startDate.plusDays( 9 ) );

			// Exercise
			api.createReservationUsingPOST( reservationInfo2 );
		}
		catch ( RestClientException rce )
		{

			// deserialize the ApiException error response to the ErrorResponse bean
			ErrorResponseDto errorResponse = ReservationErrorUtils.fromRestClientException( rce );

			// Validate
			assertEquals( HttpStatus.INTERNAL_SERVER_ERROR.toString(), errorResponse.getHttpStatusCode().toString() );
			assertEquals( "Incorrect error message", "There is a previous booking within the 30 days period.", errorResponse.getMessage() );

		}
	}

	@Test
	public void testCreateReservationWithBookingExceedThreeDays() throws Exception
	{
		try
		{
			// Set up
			LocalDate startDate = TestUtils.calculateStartDate();

			ReservationInfoDto reservationInfo = new ReservationInfoDto().firstName( "Peter" ).lastName( "Eng" ).email( "peterhleng@gmail.com" )
					.checkInDate( startDate.plusDays( 2 ) ).checkOutDate( startDate.plusDays( 6 ) );

			// Exercise
			api.createReservationUsingPOST( reservationInfo );
		}
		catch ( RestClientException rce )
		{

			// deserialize the ApiException error response to the ErrorResponse bean
			ErrorResponseDto errorResponse = ReservationErrorUtils.fromRestClientException( rce );

			// Validate
			assertEquals( HttpStatus.INTERNAL_SERVER_ERROR.toString(), errorResponse.getHttpStatusCode().toString() );
			assertEquals( "Incorrect error message", "Reservation can not exceed 3 days.", errorResponse.getMessage() );
		}
	}

	@Test
	public void testCreateReservationCheckInDateGreaterThanCheckoutDate() throws Exception
	{
		try
		{
			// Set up
			LocalDate startDate = TestUtils.calculateStartDate();

			ReservationInfoDto reservationInfo = new ReservationInfoDto().firstName( "Peter" ).lastName( "Eng" ).email( "peterhleng@gmail.com" )
					.checkInDate( startDate.plusDays( 6 ) ).checkOutDate( startDate.plusDays( 2 ) );

			// Exercise
			api.createReservationUsingPOST( reservationInfo );
		}
		catch ( RestClientException rce )
		{

			// deserialize the ApiException error response to the ErrorResponse bean
			ErrorResponseDto errorResponse = ReservationErrorUtils.fromRestClientException( rce );

			// Validate
			assertEquals( HttpStatus.INTERNAL_SERVER_ERROR.toString(), errorResponse.getHttpStatusCode().toString() );
			assertEquals( "Incorrect error message", "Check-in date is greater than Check-out date.", errorResponse.getMessage() );
		}
	}

	@Test
	public void testCreateReservationCheckInCheckoutDateSame() throws Exception
	{
		try
		{
			// Set up
			LocalDate startDate = TestUtils.calculateStartDate();

			ReservationInfoDto reservationInfo = new ReservationInfoDto().firstName( "Peter" ).lastName( "Eng" ).email( "peterhleng@gmail.com" )
					.checkInDate( startDate.plusDays( 6 ) ).checkOutDate( startDate.plusDays( 6 ) );

			// Exercise
			api.createReservationUsingPOST( reservationInfo );
		}
		catch ( RestClientException rce )
		{

			// deserialize the ApiException error response to the ErrorResponse bean
			ErrorResponseDto errorResponse = ReservationErrorUtils.fromRestClientException( rce );

			// Validate
			assertEquals( HttpStatus.INTERNAL_SERVER_ERROR.toString(), errorResponse.getHttpStatusCode().toString() );
			assertEquals( "Incorrect error message", "Check-in and Check-out dates are the same.", errorResponse.getMessage() );
		}
	}

	@Test
	public void testCreateReservationOutsideOfRange() throws Exception
	{
		try
		{
			// Set up
			LocalDate startDate = TestUtils.calculateStartDate();

			ReservationInfoDto reservationInfo = new ReservationInfoDto().firstName( "Peter" ).lastName( "Eng" ).email( "peterhleng@gmail.com" )
					.checkInDate( startDate.plusDays( 29 ) ).checkOutDate( startDate.plusDays( 32 ) );

			// Exercise
			api.createReservationUsingPOST( reservationInfo );
		}
		catch ( RestClientException rce )
		{

			// deserialize the ApiException error response to the ErrorResponse bean
			ErrorResponseDto errorResponse = ReservationErrorUtils.fromRestClientException( rce );

			// Validate
			assertEquals( HttpStatus.INTERNAL_SERVER_ERROR.toString(), errorResponse.getHttpStatusCode().toString() );
			assertEquals( "Incorrect error message", "Reservation outside the allowable date range.", errorResponse.getMessage() );
		}
	}

	@Test
	public void testCreateReservationWithDateNoLongerAvailableException() throws Exception
	{
		try
		{
			// Set up
			LocalDate startDate = TestUtils.calculateStartDate();

			ReservationInfoDto reservationInfo1 = new ReservationInfoDto().firstName( "Peter" ).lastName( "Eng" ).email( "tester@upgrade.com" )
					.checkInDate( startDate.plusDays( 2 ) ).checkOutDate( startDate.plusDays( 5 ) );

			confirmationIds.add( api.createReservationUsingPOST( reservationInfo1 ).getReservationId() );

			ReservationInfoDto reservationInfo2 = new ReservationInfoDto().firstName( "Peter" ).lastName( "Eng" ).email( "peterhleng@gmail.com" )
					.checkInDate( startDate.plusDays( 4 ) ).checkOutDate( startDate.plusDays( 7 ) );

			// Exercise
			api.createReservationUsingPOST( reservationInfo2 );
		}
		catch ( RestClientException rce )
		{

			// deserialize the ApiException error response to the ErrorResponse bean
			ErrorResponseDto errorResponse = ReservationErrorUtils.fromRestClientException( rce );

			// Validate
			assertEquals( HttpStatus.INTERNAL_SERVER_ERROR.toString(), errorResponse.getHttpStatusCode().toString() );
			assertEquals( "Incorrect error message", "One or more dates are no longer available for reservation.", errorResponse.getMessage() );
		}
	}
}
