package com.schwab.urlshortener.exception;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String errorCode;

	public ResourceNotFoundException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;

	}

	public String getErrorCode() {
		return errorCode;
	}

}