package com.upgrade.spring.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author peng
 * @since 12/21/21
 */
@Accessors
@Getter
@Setter
@ToString
@EqualsAndHashCode
@ApiModel( description = "Reservation Information" )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class ReservationInfoDto
{
	@ApiModelProperty( value = "First Name", required = true )
	private final @NonNull String firstName;
	@ApiModelProperty( value = "Last Name", required = true )
	private final @NonNull String lastName;
	@ApiModelProperty( value = "Email Address", required = true )
	private final @NonNull String email;
	@ApiModelProperty( value = "Check-In Date", required = true )
	private final @NonNull LocalDate checkInDate;
	@ApiModelProperty( value = "Check-Out Date", required = true )
	private final @NonNull LocalDate checkOutDate;

	@JsonCreator
	public ReservationInfoDto(
			@JsonProperty( "firstName" ) final String firstName, @JsonProperty( "lastName" ) final String lastName,
			@JsonProperty( "email" ) final String email, @JsonProperty( "checkInDate" ) final LocalDate checkInDate,
			@JsonProperty( "checkOutDate" ) final LocalDate checkOutDate )
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
	}

}
