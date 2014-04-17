package org.motechproject.hub.repository;

public interface BaseRepository {

	public Long getNextKey();
	public void setAuditFields(Object entity);
	
}
