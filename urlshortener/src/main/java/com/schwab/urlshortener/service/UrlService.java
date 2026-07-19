package com.schwab.urlshortener.service;

import com.schwab.urlshortener.dto.UrlRequest;
import com.schwab.urlshortener.dto.UrlResponse;

public interface UrlService {

	UrlResponse shortenUrl(UrlRequest request);

	String getOriginalUrl(String shortCode);

}