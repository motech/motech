package org.motechproject.batch.model;

import java.util.List;

import org.motechproject.batch.model.hibernate.BatchJobExecutionParams;

public class JobExecutionHistoryList {
	
	List<BatchJobExecutionParams> jobExecutionHistoryList;
	
	//Setters and Getters for the fields

	public List<BatchJobExecutionParams> getJobExecutionHistoryList() {
		return jobExecutionHistoryList;
	}

	public void setJobExecutionHistoryList(
			List<BatchJobExecutionParams> jobExecutionHistoryList) {
		this.jobExecutionHistoryList = jobExecutionHistoryList;
	}
}
