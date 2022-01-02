package com.upgrade.spring.repository;

import java.time.LocalDate;
import java.util.List;

import com.upgrade.spring.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ReservationRepository extends JpaRepository<Reservation, Long>
{
	List<Reservation> findByReservationId( String reservationId );

	List<Reservation> findAllByReservationDateBetween(
			LocalDate reservationDateStart,
			LocalDate reservationDateEnd );

	@Query( "select a from Reservation a where a.reservationDate >= :checkInDate and a.reservationDate < :checkOutDate and a.reservationId <> :reservationId" )
	List<Reservation> findAllByReservationBetweenDatesAndNotSameReservationId(
			@Param( "checkInDate" ) LocalDate checkInDate, @Param( "checkOutDate" ) LocalDate checkOutDate,
			@Param( "reservationId" ) String reservationId );

	List<Reservation> findAllByReservationDateBetweenAndEmail(
			LocalDate reservationDateStart, LocalDate reservationDateEnd, String email );

	@Transactional
	@Modifying
	List<Reservation> removeByReservationId( String reservationId );
}
