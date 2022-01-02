package com.upgrade.spring.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table( name = "reservations" )
public class Reservation
{

	@Id
	@GeneratedValue( strategy = GenerationType.TABLE )
	private long id;

	@Column( name = "reservationId" )
	private String reservationId;

	@Column( name = "firstName" )
	private String firstName;

	@Column( name = "lastName" )
	private String lastName;

	@Column( name = "email" )
	private String email;

	@Column( name = "reservationDate", columnDefinition = "DATE" )
	private LocalDate reservationDate;

	public Reservation( String reservationId, String firstName, String lastName, String email, LocalDate reservationDate )
	{
		this.reservationId = reservationId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.reservationDate = reservationDate;
	}
}
