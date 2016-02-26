package com.vvoros.springrest.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.vvoros.springrest.dto.error.ErrorDetail;
import com.vvoros.springrest.dto.error.ValidationError;
import com.vvoros.springrest.exception.ResourceNotFoundException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";

	@Autowired
	private MessageSource messageSource;
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorDetail> handleResourceNotFoundException(ResourceNotFoundException exception, HttpServletRequest request) {
		HttpStatus errorStatus = HttpStatus.NOT_FOUND;
		
		ErrorDetail errorDetail = new ErrorDetail();
		
		errorDetail.setTitle("Resource Not Found");
		errorDetail.setStatus(errorStatus.value());
		errorDetail.setDetail(exception.getMessage());
		errorDetail.setPath(getRequestPath(request));
		errorDetail.setTimeStamp(new Date().getTime());
		errorDetail.setDeveloperMessage(exception.getClass().getName());
		
		return new ResponseEntity<>(errorDetail, errorStatus);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		ErrorDetail errorDetail = new ErrorDetail();
		
		errorDetail.setTitle("Validation Failed");
		errorDetail.setStatus(HttpStatus.BAD_REQUEST.value());
		errorDetail.setDetail("Input validation failed");
		errorDetail.setPath(getRequestPath());
		errorDetail.setTimeStamp(new Date().getTime());
		errorDetail.setDeveloperMessage(ex.getClass().getName());
		
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		for (FieldError fieldError: fieldErrors) {
			List<ValidationError> validationErrors = errorDetail.getErrors().get(fieldError.getField());
			if (validationErrors == null) {
				validationErrors = new ArrayList<>();
				errorDetail.getErrors().put(fieldError.getField(), validationErrors);
			}
			ValidationError validationError = new ValidationError();
			validationError.setCode(fieldError.getCode());
			//validationError.setMessage(fieldError.getDefaultMessage());
			validationError.setMessage(messageSource.getMessage(fieldError, null));
			validationErrors.add(validationError);
		}
		
		return handleExceptionInternal(ex, errorDetail, headers, status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		ErrorDetail errorDetail = new ErrorDetail();
		
		errorDetail.setTitle("Message Not Readable");
		errorDetail.setStatus(status.value());
		errorDetail.setDetail(ex.getMessage());
		errorDetail.setPath(getRequestPath());
		errorDetail.setTimeStamp(new Date().getTime());
		errorDetail.setDeveloperMessage(ex.getClass().getName());
		
		return handleExceptionInternal(ex, errorDetail, headers, status, request);
	}

	private String getRequestPath(HttpServletRequest request) {
		String requestPath = (String) request.getAttribute(ERROR_REQUEST_URI);
		if (requestPath == null) {
			requestPath = request.getRequestURI();
		}
		
		return requestPath;
	}
	
	private String getRequestPath() {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		return getRequestPath(request);
	}
	
}
