package org.motechproject.batch.model;

import java.util.List;

import org.motechproject.batch.model.hibernate.BatchJob;

public class BatchJobList {
	private List<BatchJob> batchJobList;

	public List<BatchJob> getBatchJobList() {
		return batchJobList;
	}

	public void setBatchJobList(List<BatchJob> batchJobList) {
		this.batchJobList = batchJobList;
	}

}
