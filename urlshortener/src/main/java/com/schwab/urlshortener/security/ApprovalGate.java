package com.schwab.urlshortener.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ApprovalGate {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApprovalGate.class);

	public boolean approve(boolean approvedByUser) {
		LOGGER.info("Approval received : {}", approvedByUser);
		return approvedByUser;
	}

}