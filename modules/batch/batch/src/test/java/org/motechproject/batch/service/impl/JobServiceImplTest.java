package org.motechproject.batch.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.model.BatchJobDTO;
import org.motechproject.batch.model.BatchJobListDTO;
import org.motechproject.batch.model.hibernate.BatchJob;
import org.motechproject.batch.model.hibernate.BatchJobExecutionParams;
import org.motechproject.batch.model.hibernate.BatchJobParameters;
import org.motechproject.batch.model.hibernate.BatchJobStatus;
import org.motechproject.batch.repository.JobParametersRepository;
import org.motechproject.batch.repository.JobRepository;
import org.motechproject.batch.repository.JobStatusRepository;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class JobServiceImplTest {
	
	@Mock JobRepository jobRepo;
	@Mock JobStatusRepository jobStatusRepo;
	@Mock JobParametersRepository jobParameterRepo;
	List<BatchJob> listBatchJobDTO;
	BatchJobDTO batchJobDTO;
	BatchJob batchJob;
	List<BatchJobDTO> jobDtoList;
	BatchJobListDTO batchJobListDTO;
	
	@InjectMocks JobServiceImpl serviceImpl = new JobServiceImpl(); 
	BatchJobListDTO listDto = new BatchJobListDTO();
	BatchJobStatus batchJobStatus;
	String jobName = "Test Case";
	
	@Before
	 public void setUp () throws BatchException{
		
	 batchJob = new BatchJob();
	batchJobDTO = new BatchJobDTO(); 
	 batchJobListDTO = new BatchJobListDTO();
	 listBatchJobDTO = new ArrayList<>();
	 jobDtoList = new ArrayList<BatchJobDTO>();
	 batchJob.setBatchJobStatus(new BatchJobStatus());
	 batchJob.setCronExpression("0 15 10 * * ? 2014");
	 batchJob.setJobId(2l);
	 batchJob.setJobName("testJob");
	 listBatchJobDTO.add(batchJob);
	 batchJobListDTO.setBatchJobDtoList(jobDtoList);
	 when(jobRepo.getListOfJobs()).thenReturn(listBatchJobDTO);
	 batchJobStatus = new BatchJobStatus();
	 
	 }
	
	@Test
	public void getListOfJobs_success() throws BatchException
	{
		
		//List<BatchJobDTO> jobDtoList = new ArrayList<BatchJobDTO>();
		//when(jobRepo.getListOfJobs()).thenReturn(batchJobList);
		BatchJobListDTO batchJobListDTO = serviceImpl.getListOfJobs();
		assertNotNull(batchJobListDTO);
		//Assert.assertEquals(BatchJobListDTO.getBatchJobDtoList(), listDto.getBatchJobDtoList());
	}
	
	@Test
	public void getListOfJobs_withValues() throws BatchException
	{
//		batchJob.setJobId(jobId);
//		batchJob.setBatchJobStatus(batchJobStatus);
//		batchJob.setJobName(jobName);
//		batchJob.setCronExpression(cronExpression);
//		batchJobList.add(batchJob);
		//List<BatchJobDTO> jobDtoList = new ArrayList<BatchJobDTO>();
		//when(jobRepo.getListOfJobs()).thenReturn(batchJobList);
		BatchJobListDTO batchJobListDTO = serviceImpl.getListOfJobs();
		assertNotNull(batchJobListDTO);
		
		Assert.assertEquals(1,batchJobListDTO.getBatchJobDtoList().size());
	}
	
	@Test
	public void getJObExecutionHistory_success() throws BatchException
	{
		List<BatchJobExecutionParams> executionHistoryList = new ArrayList<>();
		when(jobRepo.getJobExecutionHistory(jobName)).thenReturn(executionHistoryList);
		when(jobRepo.getBatchJob(jobName)).thenReturn(batchJob);
		try {
			assertNotNull(serviceImpl.getJObExecutionHistory(jobName));
		} catch (BatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void scheduleJob_success() throws BatchException
	{
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("Test", "Case 1");
		when(jobStatusRepo.getActiveObject("active")).thenReturn(batchJobStatus);
		//serviceImpl.scheduleJob("Test Case", "0 15 10 * * ? 2014", hm);
	}
	
	@Test
	public void scheduleOneTimeJob_success() throws BatchException 
	{
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("Test", "Case 1");
		when(jobStatusRepo.getActiveObject("active")).thenReturn(batchJobStatus);
		//serviceImpl.scheduleOneTimeJob("Test Case", "22/04/2014 18:40:00", hm);
	}
	
	@Test
	public void updateJobProperty_success()
	{
		//when(jobRepo.getBatchJob("")).thenReturn(batchJob);
		List<BatchJobParameters> batchJobParametersList = new ArrayList<>();
		BatchJobParameters batchJobParameters = new BatchJobParameters();
		batchJobParameters.setBatchJob(batchJob);
		batchJobParameters.setJobParametersId(12l);
		batchJobParameters.setParameterName("Test");
		batchJobParameters.setParameterValue("Case 1");
		Date d = new Date();
		batchJobParameters.setCreateTime(d);
		batchJobParametersList.add(batchJobParameters);
		
		//when(jobParameterRepo.getjobParametersList(jobName)).thenReturn(batchJobParametersList);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("Test", "Case 1");
		try {
			serviceImpl.updateJobProperty(jobName, hm);
		} catch (BatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
