package org.motechproject.batch.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.batch.exception.ApplicationErrors;
import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.model.BatchJobDTO;
import org.motechproject.batch.model.BatchJobListDTO;
import org.motechproject.batch.model.CronJobScheduleParam;
import org.motechproject.batch.model.JobExecutionHistoryList;
import org.motechproject.batch.model.OneTimeJobScheduleParams;
import org.motechproject.batch.model.hibernate.BatchJob;
import org.motechproject.batch.model.hibernate.BatchJobExecutionParams;
import org.motechproject.batch.model.hibernate.BatchJobParameters;
import org.motechproject.batch.model.hibernate.BatchJobStatus;
import org.motechproject.batch.repository.JobParametersRepository;
import org.motechproject.batch.repository.JobRepository;
import org.motechproject.batch.repository.JobStatusRepository;
import org.motechproject.batch.service.JobService;
import org.motechproject.batch.util.BatchConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class to schedule reschedule jobs or update job parameters
 * @author Naveen
 *
 */

@Service
//@Transactional
public class JobServiceImpl implements JobService {
	//@Autowired
	private JobRepository jobRepo;
	//@Autowired
	private JobStatusRepository jobStatusRepo;
	//@Autowired
	private JobParametersRepository jobParameterRepo;
	
	
	public JobParametersRepository getJobParameterRepo() {
		return jobParameterRepo;
	}

	public void setJobParameterRepo(JobParametersRepository jobParameterRepo) {
		this.jobParameterRepo = jobParameterRepo;
	}

	public JobRepository getJobRepo() {
		return jobRepo;
	}

	public void setJobRepo(JobRepository jobRepo) {
		this.jobRepo = jobRepo;
	}
	public JobStatusRepository getJobStatusRepo() {
		return jobStatusRepo;
	}

	public void setJobStatusRepo(JobStatusRepository jobStatusRepo) {
		this.jobStatusRepo = jobStatusRepo;
	}

	
  
	@Override
	public BatchJobListDTO getListOfJobs() throws BatchException{
		BatchJobListDTO listDto = new BatchJobListDTO();
		List<BatchJob> jobList = jobRepo.getListOfJobs();
		List<BatchJobDTO> jobDtoList = null;
		if(jobList!=null)
		{
			jobDtoList = new ArrayList<BatchJobDTO>();
		for(BatchJob batchJob : jobList)
		{
			
			BatchJobDTO batchJobDto = new BatchJobDTO();
			batchJobDto.setJobId(batchJob.getJobId());
			batchJobDto.setJobName(batchJob.getJobName());
			batchJobDto.setCronExpression(batchJob.getCronExpression());
			batchJobDto.setCreateTime(batchJob.getCreateTime());
			batchJobDto.setLastUpdated(batchJob.getLastUpdated());
			jobDtoList.add(batchJobDto);
			
		}
		}
		
		listDto.setBatchJobDtoList(jobDtoList);
		
		return listDto;
	}

	@Override
	public JobExecutionHistoryList getJObExecutionHistory(String jobName) throws BatchException{
		
		boolean jobExist = jobRepo.checkBatchJob(jobName);
		if(jobExist == false)
				throw new BatchException(ApplicationErrors.JOB_NOT_FOUND);
		
		List<BatchJobExecutionParams> executionHistoryList = jobRepo.getJobExecutionHistory(jobName);
		//BatchJob batchJob = jobRepo.getBatchJob(jobName);
		JobExecutionHistoryList jobExecutionHistoryListDto = new JobExecutionHistoryList();
		 
		//if(executionHistoryList ==)
		//List<JobExecutionHistoryDTO> jobExecutionHistoryList = new ArrayList<JobExecutionHistoryDTO>();
		 
		/*for(BatchJobExecutionParams executionJob : executionHistoryList)
		{
			JobExecutionHistoryDTO executionHistoryDTO = new JobExecutionHistoryDTO();
			
			//Setting values of the fields in DTO
			
			executionHistoryDTO.setJobExecutionId(executionJob.getJobExecutionId());
			executionHistoryDTO.setVersion(executionJob.getVersion());
		
			executionHistoryDTO.setStartTime(executionJob.getStartTime());
			executionHistoryDTO.setEndTime(executionJob.getEndTime());
			executionHistoryDTO.setStatus(executionJob.getStatus());
			executionHistoryDTO.setExitCode(executionJob.getExitCode());
			executionHistoryDTO.setExitMessage(executionJob.getExitMessage());
			executionHistoryDTO.setLastUpdated(executionJob.getLastUpdated());
			
			executionHistoryDTO.setCreatedBy(executionJob.getCreatedBy());
			executionHistoryDTO.setLastUpdatedBy(executionJob.getLastUpdatedBy());
			
			jobExecutionHistoryList.add(executionHistoryDTO);
			
		}*/
		jobExecutionHistoryListDto.setJobExecutionHistoryList(executionHistoryList);
		
		return jobExecutionHistoryListDto;
	}

	@Override
	public void scheduleJob(CronJobScheduleParam params) throws BatchException{
			
			BatchJobStatus batchJobStatus = jobStatusRepo.getActiveObject(BatchConstants.ACTIVE_STATUS);
		
			BatchJob batchJob = new BatchJob();
			batchJob.setJobId(jobRepo.getNextKey());
			batchJob.setCronExpression(params.getCronExpression());
			batchJob.setJobName(params.getJobName());
			batchJob.setBatchJobStatus(batchJobStatus);
			
		    jobRepo.saveOrUpdate(batchJob);
		    
		    for(String key : params.getParamsMap().keySet())
		    	{
		    		
				    BatchJobParameters batchJobParms = new BatchJobParameters();
				    batchJobParms.setJobParametersId(jobParameterRepo.getNextKey());
				    batchJobParms.setBatchJob(batchJob);
				    batchJobParms.setParameterName(key);
				    batchJobParms.setParameterValue(params.getParamsMap().get(key));
				    
				    jobParameterRepo.saveOrUpdate(batchJobParms);
		    	}
		    
		
		
	}

	@Override
	public void scheduleOneTimeJob(OneTimeJobScheduleParams params) throws BatchException {
	
			DateTimeFormatter formatter = DateTimeFormat.forPattern(BatchConstants.DATE_FORMAT);
			DateTime dt = formatter.parseDateTime(params.getDate());
			String cronString = getCronString(dt);
			BatchJobStatus batchJobStatus = jobStatusRepo.getActiveObject(BatchConstants.ACTIVE_STATUS);
			
			BatchJob batchJob = new BatchJob();
			batchJob.setJobId(jobRepo.getNextKey());
			batchJob.setCronExpression(cronString);
			batchJob.setJobName(params.getJobName());
			batchJob.setBatchJobStatus(batchJobStatus);
			
		    jobRepo.saveOrUpdate(batchJob);
		    
		    for(String key : params.getParamsMap().keySet())
		    	{
		    		
				    BatchJobParameters batchJobParms = new BatchJobParameters();
				    batchJobParms.setJobParametersId(jobParameterRepo.getNextKey());
				    batchJobParms.setBatchJob(batchJob);
				    batchJobParms.setParameterName(key);
				    batchJobParms.setParameterValue(params.getParamsMap().get(key));
				    
				    jobParameterRepo.saveOrUpdate(batchJobParms);
		    	}
	
		}
	@Override
	public void updateJobProperty(String jobName, HashMap<String, String> paramsMap) throws BatchException
		{
			BatchJob batchJob = jobRepo.getBatchJob(jobName);
			List<BatchJobParameters> batchJobParametersList = jobParameterRepo.getjobParametersList(jobName);
			List<String> keyList = new ArrayList<String>();
			for(BatchJobParameters jobParam : batchJobParametersList)
			{
				keyList.add(jobParam.getParameterName());
			}
		for(String key : paramsMap.keySet())
    	{
    		if(keyList.contains(key))
    		{
    			int index = keyList.indexOf(key);
    			BatchJobParameters batchJobParam = batchJobParametersList.get(index);
    			batchJobParam.setParameterValue(paramsMap.get(key));
    			jobParameterRepo.saveOrUpdate(batchJobParam);
    		}
    		
    		else
    		{
    			BatchJobParameters batchJobParms = new BatchJobParameters();
    		    batchJobParms.setJobParametersId(jobParameterRepo.getNextKey());
    		    batchJobParms.setBatchJob(batchJob);
    		    batchJobParms.setParameterName(key);
    		    batchJobParms.setParameterValue(paramsMap.get(key));
    		    
    		    jobParameterRepo.saveOrUpdate(batchJobParms);

    		}
		 }
		}
	
	/**
	 * returns a cron string generated from datetime parameter in the form <code>dd/MM/yyyy HH:mm:ss</code>
	 * @param date date parameter from which cron string mwill be generated
	 * @return <code>String</code> representing cron expression
	 */
	private String getCronString(DateTime date)
	{
		String cronString = "00"+" "+date.getMinuteOfHour()+" "+date.getHourOfDay()+" "+date.getDayOfMonth()+" "+date.getMonthOfYear()+" ? "+date.getYear();
		return cronString;
	}
	

}
