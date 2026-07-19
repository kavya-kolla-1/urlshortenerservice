package com.schwab.urlshortener.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schwab.urlshortener.constants.ApplicationConstants;
import com.schwab.urlshortener.constants.ApplicationErrorCodes;
import com.schwab.urlshortener.constants.ApplicationMessages;
import com.schwab.urlshortener.dto.UrlRequest;
import com.schwab.urlshortener.dto.UrlResponse;
import com.schwab.urlshortener.entity.UrlMapping;
import com.schwab.urlshortener.exception.ResourceNotFoundException;
import com.schwab.urlshortener.repository.UrlRepository;
import com.schwab.urlshortener.util.Base62Generator;

@Service
@Transactional
public class UrlServiceImpl implements UrlService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UrlServiceImpl.class);

	private final UrlRepository repository;

	public UrlServiceImpl(UrlRepository repository) {
		this.repository = repository;
	}

	@Override
	public UrlResponse shortenUrl(UrlRequest request) {
		LOGGER.info("Generating short URL.");

		repository.findByOriginalUrl(request.getOriginalUrl()).ifPresent(existing -> {
			throw new IllegalArgumentException(ApplicationMessages.DUPLICATE_URL);
		});

		String code = Base62Generator.generateShortCode(request.getOriginalUrl());
		while (repository.existsByShortCode(code)) {
			code = Base62Generator.generateShortCode(request.getOriginalUrl() + System.nanoTime());
		}

		UrlMapping mapping = new UrlMapping();
		mapping.setOriginalUrl(request.getOriginalUrl());
		mapping.setShortCode(code);
		mapping.setCreatedAt(LocalDateTime.now());
		mapping.setClickCount((long) ApplicationConstants.DEFAULT_CLICK_COUNT);

		repository.save(mapping);

		LOGGER.info("Short URL generated successfully.");

		return new UrlResponse(code, ApplicationConstants.BASE_URL + code);
	}

	@Override
	public String getOriginalUrl(String shortCode) {
		LOGGER.info("Finding URL {}", shortCode);
		UrlMapping mapping = repository.findByShortCode(shortCode)
				.orElseThrow(() -> new ResourceNotFoundException(ApplicationErrorCodes.URL_NOT_FOUND,
						ApplicationMessages.URL_NOT_FOUND));
		mapping.setClickCount(mapping.getClickCount() + 1);
		repository.save(mapping);
		return mapping.getOriginalUrl();
	}

}