package com.upgrade.spring.dto;

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

/**
 * @author peng
 * @since 12/21/21
 */
@Accessors
@Getter
@Setter
@ToString
@EqualsAndHashCode
@ApiModel( description = "Reservation Confirmation Info" )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class ReservationConfirmationDto
{
	@ApiModelProperty( value = "Reservation Id", required = true )
	private final @NonNull String reservationId;

	public ReservationConfirmationDto( @JsonProperty( "reservationId" ) final String reservationId )
	{
		this.reservationId = reservationId;
	}
}
