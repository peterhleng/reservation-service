package com.upgrade.spring.exceptionhandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import com.upgrade.spring.dto.ErrorResponseDto;
import com.upgrade.spring.exception.ReservationException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.MimeType;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order( Ordered.HIGHEST_PRECEDENCE )
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler
{

	// 400
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid( final MethodArgumentNotValidException ex, final HttpHeaders headers,
			final HttpStatus status, final WebRequest request )
	{
		return exceptionToErrorResponseDto( ex,
				getErrors( ex.getBindingResult().getFieldErrors(), ex.getBindingResult().getGlobalErrors() ).toString(),
				headers,
				HttpStatus.BAD_REQUEST,
				request,
				( m -> logger.debug( "Invalid request arguments: " + m ) ) );
	}

	@Override
	protected ResponseEntity<Object> handleBindException( final BindException ex,
			final HttpHeaders headers,
			final HttpStatus status, final WebRequest request )
	{
		return exceptionToErrorResponseDto( ex,
				getErrors( ex.getBindingResult().getFieldErrors(), ex.getBindingResult().getGlobalErrors() ).toString(),
				headers,
				HttpStatus.BAD_REQUEST,
				request,
				( m -> logger.debug( "Binding error: " + m ) ) );
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch( final TypeMismatchException ex, final HttpHeaders headers, final HttpStatus status,
			final WebRequest request )
	{
		return exceptionToErrorResponseDto( ex,
				ex.getValue() + " value for " + ex.getPropertyName() + " should be of type " + ex.getRequiredType(),
				headers,
				HttpStatus.BAD_REQUEST,
				request,
				( logger::debug ) );
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestPart( final MissingServletRequestPartException ex, final HttpHeaders headers,
			final HttpStatus status, final WebRequest request )
	{
		return exceptionToErrorResponseDto( ex,
				ex.getRequestPartName() + " part is missing",
				headers, HttpStatus.BAD_REQUEST, request,
				( logger::debug ) );
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter( final MissingServletRequestParameterException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request )
	{
		return exceptionToErrorResponseDto( ex,
				ex.getParameterName() + " parameter is missing",
				headers,
				HttpStatus.BAD_REQUEST,
				request,
				( logger::debug ) );
	}

	@ExceptionHandler( {MethodArgumentTypeMismatchException.class} )
	public ResponseEntity<Object> handleMethodArgumentTypeMismatch( final MethodArgumentTypeMismatchException ex, final WebRequest request )
	{
		return exceptionToErrorResponseDto( ex,
				ex.getName() + " should be of type " + ex.getRequiredType().getName(),
				new HttpHeaders(),
				HttpStatus.BAD_REQUEST,
				request,
				( logger::debug ) );
	}

	@ExceptionHandler( {IllegalArgumentException.class} )
	public ResponseEntity<Object> handleIllegalArgument( final IllegalArgumentException ex, final WebRequest request )
	{
		return exceptionToErrorResponseDto( ex,
				ex.getMessage(),
				new HttpHeaders(),
				HttpStatus.BAD_REQUEST,
				request,
				( logger::debug ) );
	}

	@ExceptionHandler( {ConstraintViolationException.class} )
	public ResponseEntity<Object> handleConstraintViolation( final ConstraintViolationException ex, final WebRequest request )
	{
		return exceptionToErrorResponseDto( ex,
				ex.getConstraintViolations().stream()
						.map( v -> v.getRootBeanClass().getName() + " " + v.getPropertyPath() + ": " + v.getMessage() )
						.collect( Collectors.toList() ).toString(),
				new HttpHeaders(),
				HttpStatus.BAD_REQUEST,
				request,
				( m -> logger.debug( "Constraint violation caught: " + m ) ) );
	}

	@Override
	protected ResponseEntity<Object> handleMissingPathVariable( MissingPathVariableException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request )
	{
		return exceptionToErrorResponseDto( ex,
				"Missing path variable " + ex.getVariableName() + "; path is " + request.getDescription( true ),
				headers,
				status,
				request,
				( logger::debug ) );
	}

	@Override
	protected ResponseEntity<Object> handleServletRequestBindingException( ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request )
	{
		return exceptionToErrorResponseDto( ex,
				ex.getLocalizedMessage(),
				headers,
				HttpStatus.BAD_REQUEST,
				request,
				( m -> logger.debug( "Invalid request arguments: " + m ) ) );
	}

	@Override
	protected ResponseEntity<Object> handleConversionNotSupported( ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request )
	{
		return exceptionToErrorResponseDto( ex,
				ex.getLocalizedMessage(),
				headers,
				HttpStatus.BAD_REQUEST,
				request,
				( m -> logger.debug( "Invalid request arguments: " + m ) ) );

	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable( HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request )
	{
		return exceptionToErrorResponseDto( ex,
				ex.getLocalizedMessage(),
				headers,
				HttpStatus.BAD_REQUEST,
				request,
				( m -> logger.debug( "Invalid request arguments: " + m ) ) );
	}

	// 405
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported( final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers,
			final HttpStatus status, final WebRequest request )
	{
		return exceptionToErrorResponseDto( ex,
				ex.getMethod() +
						" method is not supported for this request. Supported methods are " +
						ex.getSupportedHttpMethods().stream()
								.map( Enum::name )
								.collect( Collectors.joining( ", " ) ),
				new HttpHeaders(),
				HttpStatus.METHOD_NOT_ALLOWED,
				request,
				( logger::debug ) );
	}

	// 415
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported( final HttpMediaTypeNotSupportedException ex, final HttpHeaders headers,
			final HttpStatus status, final WebRequest request )
	{
		return exceptionToErrorResponseDto( ex,
				String.valueOf( ex.getContentType() ) +
						" media type is not supported. Supported request media types are " +
						ex.getSupportedMediaTypes().stream()
								.map( MimeType::toString )
								.collect( Collectors.joining( ", " ) ),
				new HttpHeaders(),
				HttpStatus.UNSUPPORTED_MEDIA_TYPE,
				request,
				( logger::debug ) );
	}

	// 406
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable( HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request )
	{
		logger.debug( ex.getClass().getName() + ": " + ex.getMessage() + "; " +
				String.valueOf( ex.getSupportedMediaTypes() ) + " media type is acceptable. Acceptable response media types are " +
				ex.getSupportedMediaTypes().stream()
						.map( MimeType::toString )
						.collect( Collectors.joining( ", " ) ) );
		return super.handleHttpMediaTypeNotAcceptable( ex, headers, status, request );
	}

	@ExceptionHandler( value = {ReservationException.class} )
	public ResponseEntity<Object> handleReservationException( ReservationException ex, WebRequest request )
	{
		logger.debug( "< *RestReservationExceptionHandling* in " + this.toString() + " with exception " + ex, ex );

		HttpStatus status = ex.getHttpStatusCode();

		ErrorResponseDto bodyOfResponse = new ErrorResponseDto(
				status.value(),
				ex.getMessage(),
				request.getDescription( false ).replace( "uri=", "" ) );

		return handleExceptionInternal( ex, bodyOfResponse, new HttpHeaders(), status, request );
	}

	// 501
	@ExceptionHandler( {UnsupportedOperationException.class} )
	public ResponseEntity<Object> handleUnsupportedException( final UnsupportedOperationException ex, final WebRequest request )
	{
		return exceptionToErrorResponseDto( ex,
				ex.getMessage(),
				new HttpHeaders(),
				HttpStatus.NOT_IMPLEMENTED,
				request,
				( m -> logger.debug( getUriFromWebRequest( request ) + " is not yet implemented. " + ex.getMessage() ) ) );
	}

	// 500
	@ExceptionHandler( {Exception.class} )
	public ResponseEntity<Object> handleAll( final Exception ex, final WebRequest request )
	{
		return exceptionToErrorResponseDto( ex,
				ex.getMessage(),
				new HttpHeaders(),
				HttpStatus.INTERNAL_SERVER_ERROR,
				request,
				( m -> logger.error( "The server encountered an unexpected exception", ex ) ) );
	}

	private ResponseEntity<Object> exceptionToErrorResponseDto(
			@NotNull final Exception ex,
			@NotNull final String message,
			@NotNull final HttpHeaders headers,
			@NotNull final HttpStatus status,
			@NotNull final WebRequest request,
			@NotNull final Consumer<String> logger )
	{
		logger.accept( message );
		final ErrorResponseDto apiError = new ErrorResponseDto(
				status.value(),
				message,
				getUriFromWebRequest( request ) );

		return handleExceptionInternal( ex,
				apiError,
				headers,
				status,
				request );
	}

	private String getUriFromWebRequest( WebRequest request )
	{
		return request.getDescription( false ).replace( "uri=", "" );
	}

	private List<String> getErrors( List<FieldError> fieldErrors, List<ObjectError> globalErrors )
	{
		final List<String> errors = new ArrayList<>();
		for ( final FieldError error : fieldErrors )
		{
			errors.add( error.getField() + ": " + error.getDefaultMessage() );
		}
		for ( final ObjectError error : globalErrors )
		{
			errors.add( error.getObjectName() + ": " + error.getDefaultMessage() );
		}
		return errors;
	}

}