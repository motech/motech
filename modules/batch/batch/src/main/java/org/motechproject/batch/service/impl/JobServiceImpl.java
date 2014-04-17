package org.motechproject.batch.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.model.BatchJobList;
import org.motechproject.batch.model.JobExecutionHistoryDTO;
import org.motechproject.batch.model.JobExecutionHistoryList;
import org.motechproject.batch.model.hibernate.BatchJob;
import org.motechproject.batch.model.hibernate.BatchJobExecution;
import org.motechproject.batch.model.hibernate.BatchJobExecutionParams;
import org.motechproject.batch.model.hibernate.BatchJobParameters;
import org.motechproject.batch.model.hibernate.BatchJobStatus;
import org.motechproject.batch.repository.JobRepository;
import org.motechproject.batch.repository.JobStatusRepository;
import org.motechproject.batch.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobServiceImpl implements JobService {

	@Autowired
	private JobRepository jobRepo;
	private JobStatusRepository jobStatusRepo;
	
	public JobStatusRepository getJobStatusRepo() {
		return jobStatusRepo;
	}

	public void setJobStatusRepo(JobStatusRepository jobStatusRepo) {
		this.jobStatusRepo = jobStatusRepo;
	}

	public JobRepository getJobRepo() {
		return jobRepo;
	}

	public void setJobRepo(JobRepository jobRepo) {
		this.jobRepo = jobRepo;
	}
  
	@Override
	public BatchJobList getListOfJobs() {
		BatchJobList listDto = new BatchJobList();
		List<BatchJob> jobList = jobRepo.getListOfJobs();
		/*BatchJobListDTO batchJobListDto = new BatchJobListDTO();
		List<BatchJobDTO> jobDtoList = new ArrayList<BatchJobDTO>();
		
		for(BatchJob batchJob : jobList)
		{
			
			BatchJobDTO batchJobDto = new BatchJobDTO();
			
			batchJobDto.setBatchJobStatus(batchJob.
			batchJobDto.setJobId(batchJob.getJobId());
			batchJobDto.setJobName(batchJob.getJobName());
			batchJobDto.setCronExpression(batchJob.getCronExpression());
			batchJobDto.setCreateTime(batchJob.getCreateTime());
			batchJobDto.setLastUpdated(batchJob.getLastUpdated());
			jobDtoList.add(batchJobDto);
			
		}
		
		batchJobListDto.setBatchJobDtoList(jobDtoList);*/
		listDto.setBatchJobList(jobList);
		return listDto;
	}

	@Override
	public JobExecutionHistoryList getJObExecutionHistory(String jobName) {
		
		List<BatchJobExecutionParams> executionHistoryList = jobRepo.getJobExecutionHistory(jobName);
		JobExecutionHistoryList jobExecutionHistoryListDto = new JobExecutionHistoryList();
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
	public void scheduleJob(String jobName, String cronExpression,
			HashMap<String, String> paramsMap) throws BatchException{
			
		
			jobStatusRepo.getActiveObject("active");
			//jobStatusRepo.saveOrUpdate(batchJobStatus);
						
			Long jobId = jobRepo.getNextKey();
			BatchJob batchJob = new BatchJob();
			batchJob.setJobId(jobId);
			//batchJob.setBatchJobStatus(batchJobStatus);
			batchJob.setCronExpression(cronExpression);
			batchJob.setJobName(jobName);
			
		    //jobRepo.saveOrUpdate(batchJob);
			//batchJob.setBatchJobParameterses((HashSet<BatchJobParameters>)paramsMap);
		    //BatchJobParameters batchJobParams = new BatchJobParameters(batchJob, parameterName, parameterValue, createTime, lastUpdated, createdBy, lastUpdatedBy);
		    
			//jobRepo.createSchedule();
		// TODO Auto-generated method stub
		
	}

}
