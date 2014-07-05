package org.motechproject.batch.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.mds.BatchJob;
import org.motechproject.batch.mds.BatchJobParameters;
import org.motechproject.batch.mds.BatchJobStatus;
import org.motechproject.batch.mds.service.BatchJobMDSService;
import org.motechproject.batch.mds.service.BatchJobParameterMDSService;
import org.motechproject.batch.model.BatchJobDTO;
import org.motechproject.batch.model.BatchJobListDTO;
import org.motechproject.batch.model.JobExecutionHistoryList;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.batch.core.jsr.launch.JsrJobOperator;


//@RunWith(MockitoJUnitRunner.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(BatchRuntime.class)
public class JobTriggerServiceImplTest {
	BatchJobParameterMDSService jobParameterRepo = mock(BatchJobParameterMDSService.class);
	BatchJobMDSService jobRepo = mock(BatchJobMDSService.class);
	JsrJobOperator jobOperator = mock(JsrJobOperator.class);
	//Properties jobParameters = mock(Properties.class);
	//@Mock BatchJobParameterMDSService jobParameterRepo;
	//@Mock JobOperator jobOperator;
	//@Mock Properties jobParameters;
	//@Mock BatchJobMDSService jobRepo;
	@InjectMocks JobTriggerServiceImpl jobTriggerServiceImpl = new JobTriggerServiceImpl(jobRepo, jobParameterRepo,jobOperator);
	
	List<BatchJob> listBatchJobDTO;
	BatchJobDTO batchJobDTO;
	BatchJob batchJob;
	BatchJobParameters batchJobParameters = new BatchJobParameters();
	List<BatchJobParameters> parameters = new ArrayList<BatchJobParameters>();
	List<BatchJob> batchJobs = new ArrayList<BatchJob>();
	BatchJobListDTO batchJobListDTO;
	String cronExpression;
	Date date = new Date(2014, 4, 22);
	BatchJobListDTO listDto = new BatchJobListDTO();
	BatchJobStatus batchJobStatus;
	String jobName = "Test Case";
	Object id = 4L;
	String batchJobId = "4";
	private boolean jobExists;
	@Before
	 public void setUp () throws BatchException{
	 jobExists = true;
	 
	 cronExpression = "0 15 10 * * ? 2014";
	 batchJob = new BatchJob();
	 batchJob.setBatchJobStatusId(new BatchJobStatus().getJobStatusId());
	 batchJob.setCronExpression(cronExpression);
	 batchJob.setJobName("testJob");
	 
	 batchJobs.add(batchJob);
	
	 batchJobParameters.setBatchJobId(4);
	 batchJobParameters.setParameterName("Test Case");
	 batchJobParameters.setParameterValue("hcds");
	 parameters.add(batchJobParameters);
	
	 
	 
	 batchJobDTO = new BatchJobDTO(); 
	 batchJobListDTO = new BatchJobListDTO();
	 listBatchJobDTO = new ArrayList<>();
	 
	 listBatchJobDTO.add(batchJob);
	 
	 
	}
	@Test
	public void triggerJob_success() throws BatchException {
		 PowerMockito.mockStatic(BatchRuntime.class);
		 PowerMockito.when(BatchRuntime.getJobOperator()).thenReturn(jobOperator);
		 
		 Mockito.when(jobRepo.findByJobName(jobName)).thenReturn(batchJobs);
		 Mockito.when(jobRepo.getDetachedField((BatchJob)any(), (String)any())).thenReturn(id);
		 Mockito.when(jobParameterRepo.findByJobId((Integer)anyObject())).thenReturn(parameters);
		 //Mockito.when(jobOperator.start("logAnalysis", jobParameters)).thenReturn(4l);
		 
		jobTriggerServiceImpl.triggerJob(jobName);
		//verify(jobParameters).put((String)anyObject(),(String)anyObject());
		verify(jobOperator,times(1)).start((String)any(),(Properties)any());
		
	}
	

	/**
	 * valid scenario
	 * @throws BatchException
	 */
	//TODO Remove Ignore after completing the method serviceImpl.getJObExecutionHistory(jobName)
	@Ignore
	@Test
	public void getJObExecutionHistory_success() throws BatchException
	{	
		JobExecutionHistoryList listJobExecutionHistory = jobTriggerServiceImpl.getJObExecutionHistory(jobName);
		
		assertNotNull(listJobExecutionHistory);
		assertEquals(1,listJobExecutionHistory.getJobExecutionHistoryList().size());
		
		assertNotNull(listJobExecutionHistory.getJobExecutionHistoryList().get(0).getStartTime());
		assertNotNull(listJobExecutionHistory.getJobExecutionHistoryList().get(0).getEndTime());
	}
	
	/**
	 * Invalid scenario
	 * @throws BatchException
	 */
	@Test
	@Ignore
	public void getJObExecutionHistory_catch_batch_exception() throws BatchException
	{
		//TODO test cases failing
//		//when(jobRepo.checkBatchJob(jobName)).thenReturn(false);
		try{
		JobExecutionHistoryList listJobExecutionHistory = jobTriggerServiceImpl.getJObExecutionHistory(jobName);
		}catch(BatchException e){
			assertEquals("Job not found", e.getErrorMessage());
			assertEquals(1002, e.getErrorCode());
		}
	}
}
