package org.motechproject.batch.repository;

import java.util.List;

import org.motechproject.batch.model.hibernate.BatchJob;
import org.motechproject.batch.model.hibernate.BatchJobExecution;
import org.motechproject.batch.model.hibernate.BatchJobExecutionParams;

public interface BaseRepository {

	public Long getNextKey();
	public void setAuditFields(Object entity);
	
	
	
}
