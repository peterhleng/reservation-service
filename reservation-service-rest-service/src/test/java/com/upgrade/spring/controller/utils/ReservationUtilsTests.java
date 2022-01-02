package com.upgrade.spring.controller.utils;

import com.upgrade.spring.model.Reservation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author peng
 * @since 12/24/21
 */
@RunWith(MockitoJUnitRunner.class)
public class ReservationUtilsTests {


    @Test
    public void testCalculateDaysDifference() {
        long numberOfDays = ReservationUtils.calculateDifferenceBetweenDates(LocalDate.parse("2021-12-01"), LocalDate.parse("2021-12-04"));

        Assert.assertEquals("Number of days between dates", 3, numberOfDays);
    }

    @Test
    public void testGetListOfDaysBetweenDates() {
        List<LocalDate> listOfDaysBetweenDates = ReservationUtils.getListOfDaysBetweenDates(LocalDate.parse("2021-12-01"), LocalDate.parse("2021-12-04"));
        Assert.assertEquals("Number of days between dates", 3, listOfDaysBetweenDates.size());
    }

    @Test
    public void testPurgeReservedDates() {
        List<LocalDate> fullListOfDates = new ArrayList<>();
        fullListOfDates.add( LocalDate.parse("2021-12-04") );
        fullListOfDates.add( LocalDate.parse("2021-12-05") );
        fullListOfDates.add( LocalDate.parse("2021-12-06") );
        fullListOfDates.add( LocalDate.parse("2021-12-12") );
        fullListOfDates.add( LocalDate.parse("2021-12-13") );
        fullListOfDates.add( LocalDate.parse("2021-12-14") );


        List<LocalDate> purgeListOfDates = new ArrayList<>();
        purgeListOfDates.add( LocalDate.parse("2021-12-04") );
        purgeListOfDates.add( LocalDate.parse("2021-12-12") );

        List<LocalDate> purgeReservedDates = ReservationUtils.purgeReservedDates( fullListOfDates, purgeListOfDates);

        Assert.assertEquals("Purged dates are not the same", 4, purgeReservedDates.size());

    }

    @Test
    public void testGetBookedDatesFromReservations() {
        List<LocalDate> expectedDatesList = new ArrayList<>();
        expectedDatesList.add( LocalDate.parse("2021-12-04") );
        expectedDatesList.add( LocalDate.parse("2021-12-05") );
        expectedDatesList.add( LocalDate.parse("2021-12-06") );
        expectedDatesList.add( LocalDate.parse("2021-12-12") );

        Reservation reservation1 = new Reservation( "", "","","", LocalDate.parse("2021-12-04"));
        Reservation reservation2 = new Reservation( "", "","","", LocalDate.parse("2021-12-05"));
        Reservation reservation3 = new Reservation( "", "","","", LocalDate.parse("2021-12-06"));
        Reservation reservation4 = new Reservation( "", "","","", LocalDate.parse("2021-12-12"));


        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation1 );
        reservations.add(reservation2 );
        reservations.add(reservation3 );
        reservations.add(reservation4 );

        List<LocalDate> actualDates = ReservationUtils.getBookedDatesFromReservations(reservations);

        Assert.assertTrue("Dates are not the same", expectedDatesList.containsAll(actualDates));


    }

}
