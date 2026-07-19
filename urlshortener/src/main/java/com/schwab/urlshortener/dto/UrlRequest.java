package com.schwab.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UrlRequest {

	@NotBlank(message = "Original URL is mandatory")
	@Size(max = 2048)
	@Pattern(regexp = "^(http|https)://.*$", message = "Invalid URL format")
	private String originalUrl;

	public UrlRequest() {
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}
	
}