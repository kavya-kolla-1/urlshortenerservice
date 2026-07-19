package com.schwab.urlshortener.controller;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.schwab.urlshortener.common.ApiResponse;
import com.schwab.urlshortener.constants.ApplicationMessages;
import com.schwab.urlshortener.dto.UrlRequest;
import com.schwab.urlshortener.dto.UrlResponse;
import com.schwab.urlshortener.service.UrlService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/url")
public class UrlController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UrlController.class);

	private final UrlService urlService;

	public UrlController(UrlService urlService) {
		this.urlService = urlService;
	}

	@PostMapping("/shorten")
	public ResponseEntity<ApiResponse<UrlResponse>> shortenUrl(@Valid @RequestBody UrlRequest request) {
		LOGGER.info("Received shorten URL request.");

		UrlResponse response = urlService.shortenUrl(request);
		ApiResponse<UrlResponse> apiResponse = new ApiResponse<>(true, ApplicationMessages.URL_CREATED, response);

		return ResponseEntity.ok(apiResponse);
	}

	@GetMapping("/{shortCode}")
	public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
		LOGGER.info("Redirect request for {}", shortCode);

		String originalUrl = urlService.getOriginalUrl(shortCode);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(originalUrl));

		return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
	}
}