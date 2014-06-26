package org.motechproject.batch.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.mds.BatchJob;
import org.motechproject.batch.mds.BatchJobExecutionParams;
import org.motechproject.batch.mds.BatchJobParameters;
import org.motechproject.batch.mds.BatchJobStatus;
import org.motechproject.batch.mds.service.BatchJobMDSService;
import org.motechproject.batch.mds.service.BatchJobParameterMDSService;
import org.motechproject.batch.model.BatchJobDTO;
import org.motechproject.batch.model.BatchJobListDTO;
import org.motechproject.batch.model.CronJobScheduleParam;
import org.motechproject.batch.model.OneTimeJobScheduleParams;
import org.motechproject.scheduler.service.MotechSchedulerService;


@RunWith(MockitoJUnitRunner.class)
public class JobServiceImplTest {
	
	@Mock BatchJobMDSService jobRepo;
	@Mock BatchJobParameterMDSService jobParameterRepo;
	@Mock MotechSchedulerService motechSchedulerService;
	List<BatchJob> listBatchJobDTO;
	BatchJobDTO batchJobDTO;
	BatchJob batchJob;
	List<BatchJobDTO> jobDtoList;
	BatchJobListDTO batchJobListDTO;
	String cronExpression;
	String date;
	Object id = 4L;
	Date creationdate = new Date(2013, 4, 21);
	Object creationDate = creationdate;
	Date modificationdate = new Date(2014, 4, 22);
	Object modificationDate = modificationdate;
	@InjectMocks JobServiceImpl serviceImpl = new JobServiceImpl(jobRepo,jobParameterRepo, motechSchedulerService); 
	BatchJobListDTO listDto = new BatchJobListDTO();
	BatchJobStatus batchJobStatus;
	String jobName = "Test Case";
	
	private boolean jobExists;
	@Before
	 public void setUp () throws BatchException{
	 jobExists = true;
	 date = "10/09/2014 10:20:16";
	 cronExpression = "0 15 10 * * ? 2014";
	 batchJob = new BatchJob();
	 batchJob.setBatchJobStatusId(new BatchJobStatus().getJobStatusId());
	 batchJob.setCronExpression(cronExpression);
	 batchJob.setJobName("testJob");
	 List<BatchJob> batchJobs = new ArrayList<BatchJob>();
	 batchJobs.add(batchJob);
	 
	 List<BatchJobExecutionParams> executionHistoryList = new ArrayList<>();
	 BatchJobExecutionParams batchJobExecutionParams = new BatchJobExecutionParams();
	 
	 batchJobExecutionParams.setKeyName("testKey");
	 batchJobExecutionParams.setStringVal("testVal");
	 executionHistoryList.add(batchJobExecutionParams);
	 
	 batchJobStatus = new BatchJobStatus();
	 batchJobStatus.setJobStatusCode("active");
	 batchJobStatus.setJobStatusId(1);
	 
	 batchJobDTO = new BatchJobDTO(); 
	 batchJobListDTO = new BatchJobListDTO();
	 listBatchJobDTO = new ArrayList<>();
	 jobDtoList = new ArrayList<BatchJobDTO>();
	 
	 
	 
	 listBatchJobDTO.add(batchJob);
	 batchJobListDTO.setBatchJobDtoList(jobDtoList);
	 
	 when(jobRepo.findByJobName(jobName)).thenReturn(batchJobs);
	 when(jobRepo.retrieveAll()).thenReturn(batchJobs);
	 when(jobRepo.getDetachedField((BatchJob)anyObject(),(String)anyObject())).thenReturn(id);
//	 when(jobRepo.getDetachedField((BatchJob)anyObject(), "modificationDate")).thenReturn(modificationDate);
//	 when(jobRepo.getDetachedField((BatchJob)anyObject(), "creationDate")).thenReturn(creationDate);
	// when(jobRepo.checkBatchJob(jobName)).thenReturn(jobExists);
	// when(jobRepo.getJobExecutionHistory(jobName)).thenReturn(executionHistoryList);
	 //when(jobParameterRepo.getNextKey()).thenReturn(1l);
	// when(jobStatusRepo.getActiveObject((String)any())).thenReturn(batchJobStatus);
	 
	 }
	
	/**
	 * valid scenario
	 * @throws BatchException
	 */
	@Test
	public void getListOfJobs_success() throws BatchException
	{
		when(jobRepo.getDetachedField((BatchJob)anyObject(),(String)anyObject())).thenReturn(id).thenReturn(modificationDate).thenReturn(creationDate);
//		 when(jobRepo.getDetachedField((BatchJob)anyObject(), "modificationDate")).thenReturn(modificationDate);
//		 when(jobRepo.getDetachedField((BatchJob)anyObject(), "creationDate")).thenReturn(creationDate);
		BatchJobListDTO batchJobListDTO = serviceImpl.getListOfJobs();
		assertNotNull(batchJobListDTO);
		assertEquals(1, batchJobListDTO.getBatchJobDtoList().size());
		assertEquals("testJob", batchJobListDTO.getBatchJobDtoList().get(0).getJobName());
		assertEquals("0 15 10 * * ? 2014",batchJobListDTO.getBatchJobDtoList().get(0).getCronExpression());
	}
	
	/**
	 * 
	 * @throws BatchException
	 */
	/*@Test
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
		
		//Assert.assertEquals(1,batchJobListDTO.getBatchJobDtoList().size());
	}*/
	

	
	@Test
	public void scheduleJob_success() throws BatchException
	{
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("key_job", "value_job");
		CronJobScheduleParam params = new CronJobScheduleParam();
		params.setCronExpression(cronExpression);
		params.setJobName(jobName);
		params.setParamsMap(hm);
		
		
		serviceImpl.scheduleJob(params);
		verify(jobRepo).create((BatchJob)any());
		verify(jobRepo,times(1)).create((BatchJob)any());
		verify(jobParameterRepo).create((BatchJobParameters)any());
		verify(jobParameterRepo,times(1)).create((BatchJobParameters)any());
	}
	
	@Test
	public void scheduleOneTimeJob_success() throws BatchException 
	{
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("job_key", "job_value");
		OneTimeJobScheduleParams params = new OneTimeJobScheduleParams();
		params.setJobName(jobName);
		params.setParamsMap(hm);
		params.setDate(date);
		
		serviceImpl.scheduleOneTimeJob(params);
		verify(jobRepo).create((BatchJob)any());
		verify(jobRepo,times(1)).create((BatchJob)any());
		verify(jobParameterRepo).create((BatchJobParameters)any());
		verify(jobParameterRepo,times(1)).create((BatchJobParameters)any());
	}
	
	/**
	 * valid scenario(when parameterList from db matches the list sent for update)
	 * @throws BatchException
	 */
	@Test
	public void updateJobProperty_success_params_match() throws BatchException
	{
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("batch_key", "batch_value");
		List<BatchJobParameters> batchJobParametersList = new ArrayList<BatchJobParameters>();
		BatchJobParameters batchJobParam = new BatchJobParameters();
		batchJobParam.setBatchJobId(2);
		batchJobParam.setParameterName("batch_key");
		batchJobParam.setParameterValue("batch_value");
		batchJobParametersList.add(batchJobParam);
		when(jobParameterRepo.findByJobId(2)).thenReturn(batchJobParametersList);
		
		serviceImpl.updateJobProperty(jobName, hm);
		
		verify(jobParameterRepo).findByJobId((Integer)any());
		verify(jobParameterRepo,times(1)).findByJobId((Integer)any());
		verify(jobParameterRepo).create((BatchJobParameters)any());
		
	}
	
	/**
	 * valid scenario(when parameterList from db does not match the list sent for update)
	 * @throws BatchException
	 */
	@Test
	public void updateJobProperty_success_params_mismatch() throws BatchException
	{
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("batch_key", "batch_value");
		List<BatchJobParameters> batchJobParametersList = new ArrayList<BatchJobParameters>();
		BatchJobParameters batchJobParam = new BatchJobParameters();
		batchJobParam.setBatchJobId(2);
		batchJobParam.setParameterName("batch_key_db");
		batchJobParam.setParameterValue("batch_value_db");
		batchJobParametersList.add(batchJobParam);
		when(jobParameterRepo.findByJobId(4)).thenReturn(batchJobParametersList);
		
		serviceImpl.updateJobProperty(jobName, hm);
		
		verify(jobParameterRepo).findByJobId((Integer)any());
		verify(jobParameterRepo,times(1)).findByJobId((Integer)any());
		verify(jobParameterRepo).create((BatchJobParameters)any());
		
	}
	

}
