package org.motechproject.batch.dao;

import java.util.List;

import org.motechproject.batch.model.BatchJob;

public interface JobDAO {

	List<BatchJob> getListOfJobs();
	
	
}
