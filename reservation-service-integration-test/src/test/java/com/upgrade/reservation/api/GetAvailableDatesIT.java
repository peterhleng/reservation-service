package com.upgrade.reservation.api;

import com.upgrade.reservation.api.util.TestUtils;
import com.upgrade.reservation.service.restclient.api.ReservationControllerApi;
import com.upgrade.reservation.service.restclient.model.ReservationConfirmationDto;
import com.upgrade.reservation.service.restclient.model.ReservationInfoDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author peng
 * @since 12/30/21
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = {"classpath:test-context.xml"} )
public class GetAvailableDatesIT
{

    @Autowired
    private ReservationControllerApi api;


    @Test
    public void testGetAvailableDatesSuccess() throws Exception
    {
        // Setup
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = TestUtils.calculateStartDate();

        LocalDate endDate = startDate.plusDays( 30 );

        List<String> expectedDates = TestUtils.getListOfDaysBetweenDates( startDate, endDate );
        expectedDates.remove(startDate.plusDays(3).format(formatter));
        expectedDates.remove(startDate.plusDays(4).format(formatter));
        expectedDates.remove(startDate.plusDays(5).format(formatter));

        ReservationInfoDto reservationInfo = new ReservationInfoDto().firstName("Peter").lastName("Eng").email("peterhleng@gmail.com").checkInDate(startDate.plusDays(3)).checkOutDate(startDate.plusDays(6));

        ReservationConfirmationDto confirmation = api.createReservationUsingPOST(reservationInfo);

        // Exercise
        List<String> response = api.getAvailableDatesUsingGET();

        // verify
        Assert.assertEquals("Available dates are not the same",expectedDates, response);

        // clean up
        api.cancelReservationUsingDELETE(confirmation.getReservationId());
    }
}
