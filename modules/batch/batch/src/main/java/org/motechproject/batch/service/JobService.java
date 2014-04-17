package org.motechproject.batch.service;

import java.util.HashMap;

import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.model.BatchJobList;
import org.motechproject.batch.model.JobExecutionHistoryList;

public interface JobService {
	
   public BatchJobList getListOfJobs();
   public JobExecutionHistoryList getJObExecutionHistory(String jobName);
public void scheduleJob(String jobName, String cronExpression,
		HashMap<String, String> paramsMap) throws BatchException;

}
