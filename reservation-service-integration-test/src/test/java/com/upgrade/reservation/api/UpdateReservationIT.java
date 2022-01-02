package com.upgrade.reservation.api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestClientException;

import static org.junit.Assert.assertEquals;

import com.upgrade.reservation.api.util.TestUtils;
import com.upgrade.reservation.service.restclient.api.ReservationControllerApi;
import com.upgrade.reservation.service.restclient.model.ErrorResponseDto;
import com.upgrade.reservation.service.restclient.model.ReservationConfirmationDto;
import com.upgrade.reservation.service.restclient.model.ReservationErrorUtils;
import com.upgrade.reservation.service.restclient.model.ReservationInfoDto;

/**
 * @author peng
 * @since 12/24/21
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = {"classpath:test-context.xml"} )
public class UpdateReservationIT
{
	private List<String> confirmationIds = new ArrayList<>();

	@Autowired
	private ReservationControllerApi api;

	@After
	public void cleanUpEach(){
		// clean up test data
		for ( String confirmationId : confirmationIds) {
			api.cancelReservationUsingDELETE(confirmationId);
		}

		confirmationIds.clear();
	}

	@Test
	public void testUpdateReservationSuccess() throws Exception
	{
		// Set up
		LocalDate startDate = TestUtils.calculateStartDate();

		ReservationInfoDto createReservationInfo = new ReservationInfoDto().firstName("Peter").lastName("Eng").email("peterhleng@gmail.com").checkInDate(startDate.plusDays(3)).checkOutDate(startDate.plusDays(6));

		ReservationConfirmationDto response = api.createReservationUsingPOST( createReservationInfo );
		confirmationIds.add(response.getReservationId());
		// Exercise

		ReservationInfoDto updateReservationInfo = new ReservationInfoDto().firstName("Peter").lastName("Eng").email("peterhleng@gmail.com").checkInDate(startDate.plusDays(3)).checkOutDate(startDate.plusDays(6));
		api.updateReservationUsingPUT( response.getReservationId(), updateReservationInfo );

		// Validate
		assertEquals( HttpStatus.NO_CONTENT, api.getApiClient().getStatusCode() );

	}

	@Test
	public void testUpdateReservationWithReservationNotFoundException() throws Exception
	{
		try
		{
			// Set up
			LocalDate startDate = TestUtils.calculateStartDate();

			ReservationInfoDto reservationInfo2 = new ReservationInfoDto().firstName("Peter").lastName("Eng").email("peterhleng@gmail.com").checkInDate(startDate.plusDays(6)).checkOutDate(startDate.plusDays(9));

			// Exercise
			api.updateReservationUsingPUT( "test", reservationInfo2 );
		}
		catch ( RestClientException rce )
		{

			// deserialize the ApiException error response to the ErrorResponse bean
			ErrorResponseDto errorResponse = ReservationErrorUtils.fromRestClientException( rce );

			// Validate
			assertEquals( HttpStatus.NOT_FOUND.toString(), errorResponse.getHttpStatusCode().toString() );
		}
	}

	@Test
	public void testUpdateReservationWithBookingExceedThreeDays() throws Exception
	{
		try
		{
			// Set up
			LocalDate startDate = TestUtils.calculateStartDate();

			ReservationInfoDto reservationInfo = new ReservationInfoDto().firstName("Peter").lastName("Eng").email("peterhleng@gmail.com").checkInDate(startDate.plusDays(2)).checkOutDate(startDate.plusDays(6));

			// Exercise
			api.updateReservationUsingPUT( "test", reservationInfo );
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
	public void testUpdateReservationCheckInDateGreaterThanCheckoutDate() throws Exception
	{
		try
		{
			// Set up
			LocalDate startDate = TestUtils.calculateStartDate();

			ReservationInfoDto reservationInfo = new ReservationInfoDto().firstName("Peter").lastName("Eng").email("peterhleng@gmail.com").checkInDate(startDate.plusDays(6)).checkOutDate(startDate.plusDays(2));

			// Exercise
			api.updateReservationUsingPUT( "test", reservationInfo );
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
	public void testUpdateReservationCheckInCheckoutDateSame() throws Exception
	{
		try
		{
			// Set up
			LocalDate startDate = TestUtils.calculateStartDate();

			ReservationInfoDto reservationInfo = new ReservationInfoDto().firstName("Peter").lastName("Eng").email("peterhleng@gmail.com").checkInDate(startDate.plusDays(6)).checkOutDate(startDate.plusDays(6));

			// Exercise
			api.updateReservationUsingPUT( "test", reservationInfo );
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
	public void testUpdateReservationOutsideOfRange() throws Exception
	{
		try
		{
			// Set up
			LocalDate startDate = TestUtils.calculateStartDate();

			ReservationInfoDto reservationInfo = new ReservationInfoDto().firstName("Peter").lastName("Eng").email("peterhleng@gmail.com").checkInDate(startDate.plusDays(29)).checkOutDate(startDate.plusDays(32));

			// Exercise
			api.updateReservationUsingPUT( "test", reservationInfo );
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
	public void testUpdateReservationWithPreviousBookingException() throws Exception
	{
		try
		{
			// Set up
			LocalDate startDate = TestUtils.calculateStartDate();

			// original booking
			ReservationInfoDto reservationInfo1 = new ReservationInfoDto().firstName("Peter").lastName("Eng").email("peterhleng@gmail.com").checkInDate(startDate.plusDays(2)).checkOutDate(startDate.plusDays(5));

			ReservationConfirmationDto response1 = api.createReservationUsingPOST( reservationInfo1 );
			confirmationIds.add(response1.getReservationId());

			// someone else booking
			ReservationInfoDto reservationInfo2 = new ReservationInfoDto().firstName("John").lastName("Doe").email("john@upgrade.com").checkInDate(startDate.plusDays(6)).checkOutDate(startDate.plusDays(9));

			ReservationConfirmationDto response2 = api.createReservationUsingPOST( reservationInfo2 );
			confirmationIds.add(response2.getReservationId());

			// update original booking
			ReservationInfoDto reservationInfo3 = new ReservationInfoDto().firstName("Peter").lastName("Eng").email("peterhleng@gmail.com").checkInDate(startDate.plusDays(7)).checkOutDate(startDate.plusDays(10));

			// Exercise
			api.updateReservationUsingPUT( response1.getReservationId(), reservationInfo3 );
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
