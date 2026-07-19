package com.schwab.urlshortener.audit;

import java.util.List;

public interface AuditService {

	void record(AuditEvent event);

	List<AuditEvent> getAudit(String workflowId);

}