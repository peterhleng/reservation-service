package com.upgrade.spring.exception;

import org.springframework.http.HttpStatus;

/**
 * @author peng
 * @since 12/26/21
 */
public class ReservationException extends RuntimeException
{
	private HttpStatus httpStatusCode;
	private String message;

	public ReservationException( final HttpStatus httpStatusCode, final String message )
	{
		this.httpStatusCode = httpStatusCode;
		this.message = message;
	}

	public HttpStatus getHttpStatusCode()
	{
		return httpStatusCode;
	}

	@Override
	public String getMessage()
	{
		return message;
	}
}
