package com.schwab.urlshortener.constants;

public final class ApplicationErrorCodes {

	private ApplicationErrorCodes() {
	}

	public static final String URL_NOT_FOUND = "URL_404";

	public static final String VALIDATION = "URL_400";

	public static final String INTERNAL = "URL_500";

	public static final String DUPLICATE_URL = "URL_409";

}