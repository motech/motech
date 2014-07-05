package org.motechproject.batch.validation;

import static org.junit.Assert.assertNotNull;

import java.util.List;



import org.junit.Before;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class BatchValidatorTest {


	@InjectMocks BatchValidator batchValidator = new BatchValidator();
	
	private String jobName;
	private String cronExpression;
	private String date;
	private String contentType;
	@Before
	public void setUp() throws Exception{
		jobName = "Co-ordinator";
		cronExpression = "0 0 12 * * ?";
		date = "";// not used in method of service class so assigned empty string
		contentType = "text/xml";
	}
	
	/**
	 * Valid inputs scenario
	 */
	@Test 
	public void validateShedulerInputsTest(){
		List<String> errors = batchValidator.validateShedulerInputs(jobName, cronExpression);
		assertNotNull(errors);
		Assert.assertEquals(0, errors.size());
		
	}
	
	/**
	 * Invalid scenario:  with null argument value of  <code>jobName</code> null 
	 */
	@Test 
	public void validateShedulerInputsWithNullJobName(){
		jobName = null;
		List<String> errors = batchValidator.validateShedulerInputs(jobName, cronExpression);
		assertNotNull(errors);
		Assert.assertEquals(1, errors.size());
		Assert.assertEquals("Job name must be provided", errors.get(0));
	}
	
	/** 
	 * Invalid scenario: with empty argument value of  <code>jobName</code> "" 
	 */
	@Test 
	public void validateShedulerInputsWithEmptyJobName(){
		jobName = "";
		List<String> errors = batchValidator.validateShedulerInputs(jobName, cronExpression);
		assertNotNull(errors);
		Assert.assertEquals(1, errors.size());
		Assert.assertEquals("Job name must be provided", errors.get(0));
	}
	
	/** 
	 * Invalid scenario: with null argument value of <code>jobName</code> and invalid <code>cronExpression</code> any string  
	 */
	@Test 
	public void validateShedulerInputsWithNullJobNameAndInvalidCronExpression(){
		jobName = null;
		cronExpression = "0 0 3 A ?";
		List<String> errors = batchValidator.validateShedulerInputs(jobName, cronExpression);
		assertNotNull(errors);
		Assert.assertEquals(2, errors.size());
		Assert.assertEquals("Job name must be provided", errors.get(0));
		Assert.assertEquals("Job cron expression supplied is not valid", errors.get(1));
	}
	
	/**
	 * Invalid scenario: with invalid <code>cronExpression</code> any string
	 */
	@Test 
	public void validateShedulerInputsWithJobNameAndInvalidCronExpression(){
		cronExpression = "kafka 0 0 12 * * ? ";
		List<String> errors = batchValidator.validateShedulerInputs(jobName, cronExpression);
		assertNotNull(errors);
		Assert.assertEquals(1, errors.size());
		Assert.assertEquals("Job cron expression supplied is not valid", errors.get(0));
	}
	
	
	/** 
	 * Valid inputs scenario 
	 */

	@Test
	public void validateOneTimeInputsTest(){
		List<String> errors =  batchValidator.validateOneTimeInputs(jobName, date);
		Assert.assertNotNull(errors);
		Assert.assertEquals(0, errors.size());
	}
	
	/** 
	 *  Invalid scenario: with null argument value of <code>jobName</code>
	 */
	@Test
	public void validateOneTimeInputsWithNullJobName(){
		jobName = null;
		List<String> errors =  batchValidator.validateOneTimeInputs(jobName, date);
		Assert.assertNotNull(errors);
		Assert.assertEquals(1, errors.size());
		Assert.assertEquals("Job name must be provided", errors.get(0));
	}
	
	/** 
	 *  Invalid scenario: with empty argument value of <code>jobName</code> ""
	 */
	@Test
	public void validateOneTimeInputsWithEmptyJobName(){
		jobName = "";
		List<String> errors =  batchValidator.validateOneTimeInputs(jobName, date);
		Assert.assertNotNull(errors);
		Assert.assertEquals(1, errors.size());
		Assert.assertEquals("Job name must be provided", errors.get(0));
	}

	

	/** 
	 *  Valid inputs scenario 
	 */
	@Test
	public void validateUpdateInputsTest(){
		List<String> errors =  batchValidator.validateUpdateInputs(jobName);
		Assert.assertNotNull(errors);
		Assert.assertEquals(0, errors.size());
	}

	/** 
	 *  Invalid scenario: with null argument value of <code>jobName</code>
	 */
	@Test
	public void validateUpdateInputsWithNullJobName(){
		jobName = null;
		List<String> errors =  batchValidator.validateUpdateInputs(jobName);
		Assert.assertNotNull(errors);
		Assert.assertEquals(1, errors.size());
		Assert.assertEquals("Job name must be provided", errors.get(0));
	}
	
	/** 
	 *  Invalid scenario: with empty argument value of <code>jobName</code> ""
	 */
	@Test
	public void validateUpdateInputsWithEmptyJobName(){
		jobName = "";
		List<String> errors =  batchValidator.validateUpdateInputs(jobName);
		Assert.assertNotNull(errors);
		Assert.assertEquals(1, errors.size());
		Assert.assertEquals("Job name must be provided", errors.get(0));
	}
	
	
	
	/** 
	 *  Valid inputs scenario 
	 */
	@Test
	public void validateUploadInputsTest(){
		List<String> errors = batchValidator.validateUploadInputs(jobName, "text/xml");
		assertNotNull(errors);
		Assert.assertEquals(0, errors.size());		
	}
	
	/** 
	 * Invalid scenario: with invalid <code>contentType</code> any string  
	 */
	@Test 
	public void validateUploadInputsWithNullJobName(){
		jobName = null;
		List<String> errors = batchValidator.validateUploadInputs(jobName, contentType);
		assertNotNull(errors);
		Assert.assertEquals(1, errors.size());
		Assert.assertEquals("Job name must be provided", errors.get(0));
	}
	/** 
	 * Invalid scenario: with invalid <code>contentType</code> any string  
	 */
	@Test 
	public void validateUploadInputsWithInvalidContentType(){
		contentType = "text/random";
		List<String> errors = batchValidator.validateUploadInputs(jobName, contentType);
		assertNotNull(errors);
		Assert.assertEquals(1, errors.size());
		Assert.assertEquals("You must upload xml file for the job", errors.get(0));
	}
	
	/** 
	 * Invalid scenario: with null argument value of <code>jobName</code> and invalid <code>contentType</code> any string  
	 */
	@Test 
	public void validateUploadInputsWithNullJobNameAndInvalidContentType(){
		jobName = null;
		contentType = "text/xml";
		List<String> errors = batchValidator.validateUploadInputs(jobName, contentType);
		assertNotNull(errors);
		Assert.assertEquals(1, errors.size());
		Assert.assertEquals("Job name must be provided", errors.get(0));
	}
		

}
