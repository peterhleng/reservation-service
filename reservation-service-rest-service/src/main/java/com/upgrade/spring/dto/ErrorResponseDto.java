package com.upgrade.spring.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Accessors
@Getter
@Setter
@ToString
@EqualsAndHashCode
@ApiModel( description = "Error Response" )
@JsonInclude( JsonInclude.Include.NON_NULL )
public final class ErrorResponseDto
{
	@JsonProperty( "httpStatusCode" )
	private Integer httpStatusCode;

	@JsonProperty( "link" )
	private String link;

	@JsonProperty( "message" )
	private String message;

	@JsonProperty( "timestamp" )
	private OffsetDateTime timestamp;

	@JsonCreator
	public ErrorResponseDto( @JsonProperty( "httpStatusCode" ) final int httpStatusCode,
			@JsonProperty( "message" ) final String message,
			@JsonProperty( "link" ) final String link )
	{
		super();
		this.timestamp = OffsetDateTime.now(
				ZoneId.systemDefault() );
		this.httpStatusCode = httpStatusCode;
		this.message = message;
		this.link = link;
	}

}
