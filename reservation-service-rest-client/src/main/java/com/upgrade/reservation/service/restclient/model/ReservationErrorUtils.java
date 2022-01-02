package com.upgrade.reservation.service.restclient.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

/**
 * @author peng
 * @since 12/24/21
 */
public class ReservationErrorUtils
{
	public static ErrorResponseDto fromRestClientException( RestClientException rce )
	{
		/*
		In order to deserialize the json datetime into the correct datetime object you need to register a module with the object mapper.
		Depending on the setting (<dateLibrary>joda or java8</dateLibrary>) in the pom.xml file inside swagger client codegen, you either register JavaTimeModule for java8 datetime or JodaModule for joda datetime.
		 */

		final ObjectMapper objectMapper = new ObjectMapper();

		// use JodaModule to convert to Java DateTime
		objectMapper.registerModule( new JavaTimeModule() );

		String responseBody = null;
		if ( rce instanceof HttpClientErrorException )
		{
			responseBody = ( (HttpClientErrorException) rce ).getResponseBodyAsString();
		}
		else if ( rce instanceof HttpServerErrorException )
		{
			responseBody = ( (HttpServerErrorException) rce ).getResponseBodyAsString();
		}
		else
		{
			throw new RuntimeException( "Unexpected error exception type", rce );
		}

		if ( StringUtils.isEmpty( responseBody ) )
		{
			return buildErrorResponseWithEmptyResponseBody( rce );
		}
		else
		{
			try
			{
				return objectMapper.readValue( responseBody, ErrorResponseDto.class );
			}
			catch ( IOException e )
			{
				throw new RuntimeException( "Unexpected exception caught while parsing the response body", rce );
			}
		}
	}

	private static ErrorResponseDto buildErrorResponseWithEmptyResponseBody( RestClientException rce )
	{
		ErrorResponseDto errorResponseDto = new ErrorResponseDto();

		// it happens for swagger client validation error
		if ( rce instanceof HttpClientErrorException )
		{
			errorResponseDto.setHttpStatusCode( ( (HttpClientErrorException) rce ).getRawStatusCode() );
			errorResponseDto.setMessage( ( (HttpClientErrorException) rce ).getStatusText() );
		}

		return errorResponseDto;

	}
}
