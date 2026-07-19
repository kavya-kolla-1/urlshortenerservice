package com.schwab.urlshortener.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.schwab.urlshortener.common.ApiResponse;
import com.schwab.urlshortener.common.ErrorResponse;
import com.schwab.urlshortener.constants.ApplicationMessages;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
		LOGGER.error("Resource not found : {}", ex.getMessage());
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getErrorCode(),
				ex.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
				"VALIDATION_ERROR", ex.getBindingResult().getFieldError().getDefaultMessage(), request.getRequestURI());
		return ResponseEntity.badRequest().body(response);

	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex,
			HttpServletRequest request) {

		ApiResponse<Object> response = new ApiResponse<>(false, ex.getMessage(), null);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> exception(Exception ex, HttpServletRequest request) {
		LOGGER.error("Unexpected exception", ex);
		ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"INTERNAL_ERROR", ApplicationMessages.INTERNAL_SERVER_ERROR, request.getRequestURI());
		return ResponseEntity.internalServerError().body(response);
	}

}