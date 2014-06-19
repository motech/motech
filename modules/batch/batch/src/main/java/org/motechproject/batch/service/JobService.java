package org.motechproject.batch.service;

import java.util.HashMap;

import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.model.BatchJobListDTO;
import org.motechproject.batch.model.CronJobScheduleParam;
import org.motechproject.batch.model.JobExecutionHistoryList;
import org.motechproject.batch.model.OneTimeJobScheduleParams;

/**
 * Interface to schedule reschedule jobs or update job parameters
 * @author Naveen
 *
 */
public interface JobService {
	
	/**
	 * get list of scheduled jobs
	 * @return batchJobListDTO which contains list of BatchJobDTO(contains fields from BatchJob <code>class</code>)
	 */
public BatchJobListDTO getListOfJobs() throws BatchException;



/**
 * Shedule a new cron job with given job name and cron expression
 * @param jobName job name for the job to be scheduled
 * @param cronExpression cron expression for the job (specified for the timely run of the job)
 * @param paramsMap List of parameters to be used while job is being triggered
 * @throws BatchException
 */
public void scheduleJob(CronJobScheduleParam params) throws BatchException;

/**
 * Schedule a one time job, to be run once in the future
 * @param jobName job name for the job to be scheduled
 * @param date The date and time at which job will be run
 * @param paramsMap List of parameters to be used while job is being triggered
 * @throws BatchException
 */
public void scheduleOneTimeJob(OneTimeJobScheduleParams params) throws BatchException;

/**
 * Update the job parameters of the scheduled job
 * @param jobName job name for the job for which parameters to be updated
 * @param paramsMap List of parameters to be added or changed
 * @throws BatchException
 */
public void updateJobProperty(String jobName, HashMap<String, String> paramsMap) throws BatchException;

public String sayHello();


}
