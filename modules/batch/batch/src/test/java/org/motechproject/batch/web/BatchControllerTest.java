package org.motechproject.batch.web; 

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.batch.exception.BatchErrors;
import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.exception.RestException;
import org.motechproject.batch.model.BatchJobDTO;
import org.motechproject.batch.model.BatchJobListDTO;
import org.motechproject.batch.model.JobExecutionHistoryList;
import org.motechproject.batch.mds.BatchJobExecution;
import org.motechproject.batch.mds.BatchJobExecutionParams;
import org.motechproject.batch.mds.BatchJobStatus;
import org.motechproject.batch.service.JobService;
import org.motechproject.batch.service.JobTriggerService;
import org.motechproject.batch.validation.BatchValidator;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class BatchControllerTest {
	
	@Mock JobService jobService;
	@Mock JobTriggerService jobTriggerService;
	@Mock BatchValidator batchValidator;
	@InjectMocks BatchController controller = new BatchController();
	private BatchJobListDTO batchJobListDTO;
	private BatchJobDTO batchJobDTO;
	private List<BatchJobDTO> listBatchJobDTO;
	private String jobName;
	private BatchJobExecution batchJobExecutionParams;
	private JobExecutionHistoryList jobExecutionHistoryList;
	private List<BatchJobExecution> paramsList;
	private List<String> errors;
	private String cronExpression;
	private HashMap<String, String> paramsMap;
	MockMultipartFile mockMultipartFile;
	
	@Before
	public void setUp () throws BatchException{
		
	jobName = "testJob";
	cronExpression = "0 15 10 * * ? 2014";
	paramsMap = new HashMap<String, String>();
	errors = new ArrayList<String>();
	batchJobDTO = new BatchJobDTO();	
	batchJobListDTO = new BatchJobListDTO();
	listBatchJobDTO = new ArrayList<BatchJobDTO>();
	mockMultipartFile = new MockMultipartFile("fileName" , "myContent1".getBytes());
	batchJobDTO.setBatchJobStatus(new BatchJobStatus());
	batchJobDTO.setCronExpression("0 15 10 * * ? 2014");
	batchJobDTO.setJobId(2);
	batchJobDTO.setJobName("testJob");
	listBatchJobDTO.add(batchJobDTO);
	batchJobListDTO.setBatchJobDtoList(listBatchJobDTO);
	
	jobExecutionHistoryList = new JobExecutionHistoryList();
	paramsList = new ArrayList<BatchJobExecution>();
	paramsList.add(batchJobExecutionParams);
	jobExecutionHistoryList.setJobExecutionHistoryList(paramsList);
	when(jobService.getListOfJobs()).thenReturn(batchJobListDTO);
	when(jobTriggerService.getJObExecutionHistory(jobName)).thenReturn(jobExecutionHistoryList);
	when(batchValidator.validateUpdateInputs(jobName)).thenReturn(errors);
	//when(batchValidator.validateUpdateInputs(jobName)).thenReturn(errors);
	}
	
	/**
	 * valid input scenario
	 */
	@Test
	public void getJobListReturnsValidResponse()
	{
		BatchJobListDTO batchJobListDTO = controller.getJobList();
		assertNotNull(batchJobListDTO);
		assertEquals(1, batchJobListDTO.getBatchJobDtoList().size());
		assertEquals("testJob", batchJobListDTO.getBatchJobDtoList().get(0).getJobName());
	}
	
	
	@Test
	public void getjobHistoryListReturnsValidResponse() throws Exception
	{
		
		JobExecutionHistoryList jobExecutionHistoryList = controller.getjobHistoryList(jobName);
		assertNotNull(jobExecutionHistoryList);
		assertEquals(1, jobExecutionHistoryList.getJobExecutionHistoryList().size());
		
	}
	
	/**
	 * Invalid scenario: mandatory field <code>jobName</code> empty
	 * @throws Exception
	 */
	@Test
	public void getJobHistoryListEmptyJobName()
	{
		jobName = "";
		errors.add("Job name must be provided");
		when(batchValidator.validateUpdateInputs(jobName)).thenReturn(errors);
		try
		{
			JobExecutionHistoryList jobExecutionHistoryList = controller.getjobHistoryList(jobName);
		}
		catch(RestException e){
			BatchErrors be = e.getBatchException().getError();
			assertEquals(1001, be.getCode());
			assertEquals(HttpStatus.BAD_REQUEST, be.getHttpStatus());
			assertEquals("One or more input parameter(s) may be wrong", be.getMessage());
		}
		//assertEquals(, actual);
		
		
	}
	
	/**
	 * Invalid scenario: mandatory field <code>jobName</code> null
	 */
	
	@Test
	public void getJobHistoryListNullJobName()
	{
		jobName = null;
		when(batchValidator.validateUpdateInputs(jobName)).thenReturn(errors);
		errors.add("Job name must be provided");
	
	try
		{
			JobExecutionHistoryList jobExecutionHistoryList = controller.getjobHistoryList(jobName);
		}
	catch(RestException e)
		{
			BatchErrors be = e.getBatchException().getError();
			assertEquals(1001, be.getCode());
			assertEquals(HttpStatus.BAD_REQUEST, be.getHttpStatus());
			assertEquals("One or more input parameter(s) may be wrong", be.getMessage());
		}
	
	}
	
	/**
	 * valid input scenario
	 */
	@Test
	public void triggerJobReturnsValidResponse()
	{
		errors.clear();
		when(batchValidator.validateUpdateInputs(jobName)).thenReturn(errors);
		
			controller.triggerJob(jobName);
		
	}
	
	/**
	 * Invalid scenario: mandatory field <code>jobName</code> empty
	 * @throws BatchException 
	 * @throws Exception
	 */
	@Test
	public void triggerJobWithEmptyJOBName()
	{
		jobName = "";
		errors.clear();
		errors.add("Job name must be provided");
		when(batchValidator.validateUpdateInputs(jobName)).thenReturn(errors);
		try {
			controller.triggerJob(jobName);
		} catch (RestException e) {
			BatchErrors be = e.getBatchException().getError();
			assertEquals(1001, be.getCode());
			assertEquals(HttpStatus.BAD_REQUEST, be.getHttpStatus());
			assertEquals("One or more input parameter(s) may be wrong", be.getMessage());
		}
	}
	
	
	/**
	 * Invalid scenario: mandatory field <code>jobName</code> null
	 */
	@Test
	public void triggerJobWithNullJOBName()
	{
		jobName = null;
		errors.clear();
		errors.add("Job name must be provided");
		when(batchValidator.validateUpdateInputs(jobName)).thenReturn(errors);
		try {
			controller.triggerJob(jobName);
		} catch (RestException e) {
			BatchErrors be = e.getBatchException().getError();
			assertEquals(1001, be.getCode());
			assertEquals(HttpStatus.BAD_REQUEST, be.getHttpStatus());
			assertEquals("One or more input parameter(s) may be wrong", be.getMessage());
		}
	}
	
	@Test
	public void scheduleCronjobWithValidInputs()
	{
		errors.clear();
		errors.add("Job name must be provided");
		when(batchValidator.validateUpdateInputs(jobName)).thenReturn(errors);
		//controller.scheduleCronJob(jobName, cronExpression, paramsMap);
		//TODO what values to assert;
	}
	
	/**
	 * Invalid scenario: mandotary field <code>cronExpression</code> invalid 
	 */
	@Test
	public void scheduleCronjobWithInvalidCronExpression()
	{
		cronExpression ="2434 ? 998?*";  
		errors.clear();
		errors.add("Job cron expression supplied is not valid");
		when(batchValidator.validateShedulerInputs(jobName, cronExpression)).thenReturn(errors);
		try
		{
			//controller.scheduleCronJob(jobName, cronExpression, paramsMap);
		}
		catch(RestException e){
			BatchErrors be = e.getBatchException().getError();
			assertEquals(1001, be.getCode());
			assertEquals(HttpStatus.BAD_REQUEST, be.getHttpStatus());
			assertEquals("One or more input parameter(s) may be wrong", be.getMessage());
		}
	}
	
	
	@Test
	public void scheduleOneTimejobWithValidInputs()
	{
		errors.clear();
		when(batchValidator.validateOneTimeInputs(jobName, "05/10/2014 10:08:47")).thenReturn(errors);
		//controller.scheduleOneTimeJob(jobName, "05/10/2014 10:08:47", paramsMap);
		//TODO what values to assert;
	}
	
	
	@Test
	public void scheduleOneTimejobWithEmptyJobName()
	{
		jobName = "";  
		errors.clear();
		errors.add("Job name must be provided");
		when(batchValidator.validateOneTimeInputs(jobName, "05/10/2014 10:08:47")).thenReturn(errors);
		try
		{
			//controller.scheduleOneTimeJob(jobName, "05/10/2014 10:08:47", paramsMap);
		}
		catch(RestException e){
			BatchErrors be = e.getBatchException().getError();
			assertEquals(1001, be.getCode());
			assertEquals(HttpStatus.BAD_REQUEST, be.getHttpStatus());
			assertEquals("One or more input parameter(s) may be wrong", be.getMessage());
		}
	}
	
	@Test
	public void updateJobPropertyWithValidInputs()
	{  
		errors.clear();
		when(batchValidator.validateUpdateInputs(jobName)).thenReturn(errors);
		
			//controller.updateJobProperty(jobName, paramsMap);
		
	}
	
}
