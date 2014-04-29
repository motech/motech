package org.motechproject.batch.model;

import java.util.List;
import org.motechproject.batch.model.hibernate.BatchJob;

/**
 * 
 * @author Naveen
 *
 */
public class BatchJobList {
	private List<BatchJob> batchJobsList;

	public List<BatchJob> getBatchJobList() {
		return batchJobsList;
	}

	public void setBatchJobList(List<BatchJob> batchJobsList) {
		this.batchJobsList = batchJobsList;
	}

}
