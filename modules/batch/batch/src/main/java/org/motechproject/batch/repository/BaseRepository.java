package org.motechproject.batch.repository;

import java.util.List;

import org.motechproject.batch.model.hibernate.BatchJob;
import org.motechproject.batch.model.hibernate.BatchJobExecution;
import org.motechproject.batch.model.hibernate.BatchJobExecutionParams;

/**
 * Interface to set the common fields of the objects to be stored in database and to get the primary key sequence
 * @author Naveen
 *
 */
public interface BaseRepository {

	/**
	 * Returns the <code>long</code> value of the next applicable primary key of the table 
	 * @return
	 */
	public Long getNextKey();
	
    /**
     * Set the common fields of the objects to be stored in the database
     * @param entity
     */
	public void setAuditFields(Object entity);
	
	
	
}
