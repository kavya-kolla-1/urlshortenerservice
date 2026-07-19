package com.schwab.urlshortener.audit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuditServiceImpl.class);

	private final ConcurrentHashMap<String, List<AuditEvent>> audits = new ConcurrentHashMap<>();

	@Override
	public void record(AuditEvent event) {
		audits.computeIfAbsent(event.getWorkflowId(), k -> new ArrayList<>()).add(event);
		LOGGER.info("Audit Event : {} {}", event.getStage(), event.getStatus());
	}

	@Override
	public List<AuditEvent> getAudit(String workflowId) {
		return audits.getOrDefault(workflowId, new ArrayList<>());
	}

}