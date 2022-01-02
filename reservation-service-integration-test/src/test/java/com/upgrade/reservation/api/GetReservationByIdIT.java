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

/**
 * @author peng
 * @since 12/30/21
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = {"classpath:test-context.xml"} )
public class GetReservationByIdIT
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
	public void testGetReservationSuccess() throws Exception
	{
		// Set up
		LocalDate startDate = TestUtils.calculateStartDate();

		ReservationInfoDto reservationInfo = new ReservationInfoDto().firstName( "Peter" ).lastName( "Eng" ).email( "peterhleng@gmail.com" )
				.checkInDate( startDate.plusDays( 3 ) ).checkOutDate( startDate.plusDays( 6 ) );

		ReservationConfirmationDto response = api.createReservationUsingPOST( reservationInfo );
		confirmationIds.add( response.getReservationId() );

		// Exercise
		ReservationInfoDto reservationInfoResponse = api.getReservationByIdUsingGET( response.getReservationId() );

		// Validate
		assertEquals( HttpStatus.OK, api.getApiClient().getStatusCode() );
		assertEquals( "First Name is incorrect", "Peter", reservationInfoResponse.getFirstName() );
		assertEquals( "Last Name is incorrect", "Eng", reservationInfoResponse.getLastName() );
		assertEquals( "Email is incorrect", "peterhleng@gmail.com", reservationInfoResponse.getEmail() );
		assertEquals( "Check-in date is incorrect", startDate.plusDays( 3 ), reservationInfoResponse.getCheckInDate() );
		assertEquals( "Check-out date is incorrect", startDate.plusDays( 6 ), reservationInfoResponse.getCheckOutDate() );

	}

	@Test
	public void testReservationNotExist() throws Exception
	{
		try
		{
			// Exercise
			api.getReservationByIdUsingGET( "test" );
		}
		catch ( RestClientException rce )
		{

			// deserialize the ApiException error response to the ErrorResponse bean
			ErrorResponseDto errorResponse = ReservationErrorUtils.fromRestClientException( rce );

			// Validate
			assertEquals( HttpStatus.NOT_FOUND.toString(), errorResponse.getHttpStatusCode().toString() );
		}

	}
}
