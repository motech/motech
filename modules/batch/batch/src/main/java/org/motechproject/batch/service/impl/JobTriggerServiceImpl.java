package org.motechproject.batch.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.batch.operations.JobOperator;
//import javax.batch.runtime.BatchRuntime;

import javax.batch.runtime.BatchRuntime;

import org.motechproject.batch.model.hibernate.BatchJobParameters;
import org.motechproject.batch.repository.JobParametersRepository;
import org.motechproject.batch.service.JobTriggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class JobTriggerServiceImpl implements JobTriggerService {
	
	
	@Autowired
	private JobParametersRepository jobParameterRepo;

	public JobParametersRepository getJobParameterRepo() {
		return jobParameterRepo;
	}

	public void setJobParameterRepo(JobParametersRepository jobParameterRepo) {
		this.jobParameterRepo = jobParameterRepo;
	}

	
	
	
	
	@Override
	public void triggerJob(String jobName, Date date) {
			//List<BatchJobParameters> parametersList = jobParameterRepo.getjobParametersList(jobName);
			//JsrJobOperator jsr = new JsrJobOperator();
			JobOperator jobOperator = BatchRuntime.getJobOperator();
			Properties jobParameters = new Properties();
			
//			for(BatchJobParameters batchJobParameter : parametersList )
//				{
//					jobParameters.put(batchJobParameter.getParameterName(),batchJobParameter.getParameterValue());
//				}
			      
			Long executionId = null;
			
			executionId = jobOperator.start("logAnalysis", jobParameters);
			System.out.println(executionId.toString()+","+executionId.floatValue());
			//jsr.setJobOperator((org.springframework.batch.core.launch.JobOperator) jobOperator);
			//JobOperator jobOperator = jsr.setJobOperator(jobOperator);
			
			
			
			
					// TODO Auto-generated method stub
		
	}

	

}
