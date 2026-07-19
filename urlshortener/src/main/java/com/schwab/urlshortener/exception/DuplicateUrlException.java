package com.schwab.urlshortener.exception;

public class DuplicateUrlException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DuplicateUrlException(String message) {
		super(message);
	}

}