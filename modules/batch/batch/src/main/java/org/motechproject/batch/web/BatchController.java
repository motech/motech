package org.motechproject.batch.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.motechproject.batch.exception.ApplicationErrors;
import org.motechproject.batch.exception.BatchError;
import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.exception.RestException;
import org.motechproject.batch.model.BatchJobList;
import org.motechproject.batch.model.JobExecutionHistoryList;
import org.motechproject.batch.service.JobService;
import org.motechproject.batch.service.JobTriggerService;
import org.motechproject.batch.validation.BatchValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/batch")
public class BatchController {
	
	@Autowired
	JobService jobService;
	
	public JobService getJobService() {
		return jobService;
	}

	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}
	
	@Autowired
	JobTriggerService jobTriggerService;



	public JobTriggerService getJobTriggerService() {
		return jobTriggerService;
	}

	public void setJobTriggerService(JobTriggerService jobTriggerService) {
		this.jobTriggerService = jobTriggerService;
	}

	private String xmlPath;
	
	public String getXmlPath() {
		return xmlPath;
	}

	@Value("${xml.path}")
	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}

	@Autowired
	private BatchValidator batchValidator;
	
	public BatchValidator getBatchValidator() {
		return batchValidator;
	}

	public void setBatchValidator(BatchValidator batchValidator) {
		this.batchValidator = batchValidator;
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/jobs", method = RequestMethod.GET)
	@ResponseBody public BatchJobList getJobList()
			{
		        System.out.println("inside controller");
				BatchJobList batchJobList = new BatchJobList();	
			try
				{
					batchJobList = jobService.getListOfJobs();
				}
			catch(Exception e)
			{
				
			}
			return batchJobList;
				
		
		
	}
	
	
	
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/jobHistory", method = RequestMethod.POST, headers = "Content-Type=application/json")
	@ResponseBody public JobExecutionHistoryList getjobHistoryList(
			@RequestParam String jobName) 
			throws BatchException {
		
				JobExecutionHistoryList jobExecutionHistoryList = jobService.getJObExecutionHistory(jobName);
				
				return jobExecutionHistoryList;
	}
	
	
	
	@RequestMapping(value = "/upload", method=RequestMethod.POST)
	public String handleImageUploadForPromotions(HttpServletRequest request,
			HttpServletResponse response,  
	    @RequestParam("file") MultipartFile file){
		
		System.out.println("inside controller");
		
		try {
			byte[] bytes = file.getBytes();
			BufferedOutputStream stream;
			System.out.println(xmlPath);
			stream = new BufferedOutputStream(new FileOutputStream(new File(xmlPath,file.getOriginalFilename())));
			 stream.write(bytes);
		     stream.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
       return null;
		
	}
    
	
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/trigger",method = RequestMethod.POST)
	@ResponseBody public void triggerJob(
			@RequestParam String jobName) 
			throws BatchException {
		     
				//TODO
		try {
		System.out.println("inside controller");
				jobTriggerService.triggerJob(jobName, new Date());
		} catch(Exception e) {
			e.printStackTrace();
			throw new RestException(e, e.getMessage());
		}
			
	}
	
	
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/schedulecronjob",method = RequestMethod.POST)
	@ResponseBody public void ScheduleCronJob(
			@RequestParam String jobName, String cronExpression, HashMap<String, String> paramsMap)
			throws BatchException {
		     
				
		try {
			  	List<String> errors = batchValidator.validateShedulerInputs(jobName, cronExpression);
			  	
			  	if (!errors.isEmpty()) {
					throw new BatchException(ApplicationErrors.BAD_REQUEST, errors.toString());
				}
			  	jobService.scheduleJob(jobName, cronExpression, paramsMap );
			} 
			catch(BatchException e) {
			throw new RestException(e, e.getMessage());
		}
			
	}

	

	@ExceptionHandler(value = { RestException.class })
	@ResponseBody public BatchError restExceptionHandler(RestException ex,
			HttpServletResponse response) {
		BatchError error = new BatchError();

		try {
			response.setStatus(ex.getHttpStatus().value());
			error.setErrorCode(String.valueOf(ex.getBatchException()
					.getErrorCode()));
			error.setErrorMessage(ex.getBatchException().getErrorMessage());
			error.setApplication("motech-platform-batch");

		} catch (Exception e) {
			// log
			// log
		}
		return error;
	}
	
	
	}
