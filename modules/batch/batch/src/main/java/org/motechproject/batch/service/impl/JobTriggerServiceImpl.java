package org.motechproject.batch.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.batch.operations.JobOperator;
//import javax.batch.runtime.BatchRuntime;

import javax.batch.operations.JobSecurityException;
import javax.batch.operations.JobStartException;
import javax.batch.runtime.BatchRuntime;

import org.motechproject.batch.exception.ApplicationErrors;
import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.model.hibernate.BatchJobParameters;
import org.motechproject.batch.repository.JobParametersRepository;
import org.motechproject.batch.repository.JobRepository;
import org.motechproject.batch.service.JobTriggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class to perform the trigger operation for all types of jobs
 * @author Naveen
 *
 */
@Service
//@Transactional
public class JobTriggerServiceImpl implements JobTriggerService {
	
	//@Autowired
	JobRepository jobRepo;
	
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





	
	
	@Override
	public void triggerJob(String jobName, Date date) throws BatchException {
			
			boolean jobExists = jobRepo.checkBatchJob(jobName);
			if(jobExists == false)
				throw new BatchException(ApplicationErrors.JOB_NOT_FOUND);
			
			List<BatchJobParameters> parametersList = jobParameterRepo.getjobParametersList(jobName);
			JobOperator jobOperator = BatchRuntime.getJobOperator();
			Properties jobParameters = new Properties();
			
			for(BatchJobParameters batchJobParameter : parametersList)
				{
					jobParameters.put(batchJobParameter.getParameterName(),batchJobParameter.getParameterValue());
				}
			      
			Long executionId = null;
			try{
			executionId = jobOperator.start("logAnalysis", jobParameters);
			}catch(JobStartException | JobSecurityException e){
				throw new BatchException(ApplicationErrors.JOB_TRIGGER_FAILED, e.getCause());
			}
			
			// TODO Implement the datetime
		
	}

	

}
