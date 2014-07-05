package org.motechproject.batch.web;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.motechproject.batch.exception.ApplicationErrors;
import org.motechproject.batch.exception.BatchError;
import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.exception.RestException;
import org.motechproject.batch.model.BatchJobListDTO;
import org.motechproject.batch.model.CronJobScheduleParam;
import org.motechproject.batch.model.JobExecutionHistoryList;
import org.motechproject.batch.model.OneTimeJobScheduleParams;
import org.motechproject.batch.service.FileUploadService;
import org.motechproject.batch.service.JobService;
import org.motechproject.batch.service.JobTriggerService;
import org.motechproject.batch.validation.BatchValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller class to perform all the batch job operations
 * 
 * @author Naveen
 * 
 */
@Controller
@RequestMapping("/batch")
public class BatchController {

	private final static Logger LOGGER = Logger
			.getLogger(BatchController.class);

	@Autowired
	JobService jobService;

	@Autowired
	FileUploadService fileUploadService;

	@Value("${xml.path}")
	private String xmlPath;

	public String getXmlPath() {
		return xmlPath;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}

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

	@Autowired
	private BatchValidator batchValidator;

	public BatchValidator getBatchValidator() {
		return batchValidator;
	}

	public void setBatchValidator(BatchValidator batchValidator) {
		this.batchValidator = batchValidator;
	}

	/**
	 * To get list of all the scheduled jobs
	 * 
	 * @return List of BatchJob.
	 */
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/jobs", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public BatchJobListDTO getJobList() {
		LOGGER.info("Request to get list of batch jobs started");
		StopWatch sw = new StopWatch();
		sw.start();
		BatchJobListDTO batchJobList = null;
		try {
			batchJobList = jobService.getListOfJobs();
			return batchJobList;
		} catch (BatchException e) {
			LOGGER.error("Error occured while processing request to get list of jobs");
			throw new RestException(e, e.getMessage());
		} finally {
			LOGGER.info("Request to get list of batch jobs ended. Time taken (ms) = "
					+ sw.getTime());
			sw.stop();
		}

	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/jobHistory/{jobName}", method = RequestMethod.GET)
	@ResponseBody
	public JobExecutionHistoryList getjobHistoryList(
			@PathVariable("jobName") String jobName) {
		LOGGER.info("Request to get execution history of job " + jobName
				+ " started");
		StopWatch sw = new StopWatch();
		sw.start();
		JobExecutionHistoryList jobExecutionHistoryList = null;
		try {
			List<String> errors = batchValidator.validateUpdateInputs(jobName);
			if (!errors.isEmpty()) {
				// TODO log warn level
				throw new BatchException(ApplicationErrors.BAD_REQUEST,
						errors.toString());
			}
			jobExecutionHistoryList = jobTriggerService
					.getJObExecutionHistory(jobName);
			return jobExecutionHistoryList;
		} catch (BatchException e) {
			LOGGER.error("Error occured while processing request to get execution history for job "
					+ jobName);
			throw new RestException(e, e.getMessage());
		}

		finally {
			LOGGER.info("Request to get execution history for job with jobname: "
					+ jobName + " ended. Time taken (ms) = " + sw.getTime());
			sw.stop();
		}

	}

	/**
	 * Uploads the xml file the batch job operation
	 * 
	 * @param file
	 *            Input xml file for the job
	 * @param jobName
	 *            jobname for which xml file needs to be uploaded
	 */
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseBody
	public void handleImageUploadForJobs(
			@RequestParam("file") MultipartFile file, String jobName) {

		LOGGER.info("Request to upload xml file of job: " + jobName
				+ " started");
		StopWatch sw = new StopWatch();
		sw.start();

		try {
			List<String> errors = batchValidator.validateUploadInputs(jobName,
					file.getContentType());

			if (!errors.isEmpty()) {
				throw new BatchException(ApplicationErrors.BAD_REQUEST,
						errors.toString());
			}

			fileUploadService.uploadFile(jobName, file, xmlPath);

		} catch (BatchException e) {
			LOGGER.error("Error occured while processing request to upload xml file for job: "
					+ jobName);
			throw new RestException(e, e.getMessage());
		}

		finally {
			LOGGER.info("Request to upload xml file for job: " + jobName
					+ " ended. Time taken (ms) = " + sw.getTime());
			sw.stop();
		}
		

	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/trigger/{jobName}", method = RequestMethod.GET)
	@ResponseBody
	public void triggerJob(@PathVariable("jobName") String jobName) {

		LOGGER.info("Request to trigegr a job: " + jobName + " started");
		StopWatch sw = new StopWatch();
		sw.start();
		try {
			List<String> errors = batchValidator.validateUpdateInputs(jobName);
			if (!errors.isEmpty()) {
				throw new BatchException(ApplicationErrors.BAD_REQUEST,
						errors.toString());
			}
			jobTriggerService.triggerJob(jobName);
		} catch (BatchException e) {
			LOGGER.error("Error occured while processing request to trigger job: "
					+ jobName);
			throw new RestException(e, e.getMessage());
		} finally {
			LOGGER.info("Request to trigger a job: " + jobName
					+ " ended. Time taken (ms) = " + sw.getTime());
			sw.stop();
		}
	}

	/**
	 * Schedule a cron job given job name, cron expression and parameters for
	 * the job
	 * 
	 * @param jobName
	 *            jobName for the job to be scheduled
	 * @param cronExpression
	 *            cron expression for the job
	 * @param paramsMap
	 * @throws BatchException
	 */
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/schedulecronjob", method = RequestMethod.POST, headers = "Content-Type=application/json")
	@ResponseBody
	public void scheduleCronJob(@RequestBody CronJobScheduleParam params) {

		LOGGER.info("Request to schedule a cron job for job: "
				+ params.getJobName() + "with cron expression"
				+ params.getCronExpression() + " started");
		StopWatch sw = new StopWatch();
		sw.start();
		try {
			List<String> errors = batchValidator.validateShedulerInputs(
					params.getJobName(), params.getCronExpression());

			if (!errors.isEmpty()) {
				throw new BatchException(ApplicationErrors.BAD_REQUEST,
						errors.toString());
			}
			jobService.scheduleJob(params);
		} catch (BatchException e) {
			LOGGER.error("Error occured while processing request to schedule a cron job for job: "
					+ params.getJobName()
					+ "with cron expression"
					+ params.getCronExpression());
			throw new RestException(e, e.getMessage());
		} finally {
			LOGGER.info("Request to schedule a cron job for job: "
					+ params.getJobName() + " ended. Time taken (ms) = "
					+ sw.getTime());
			sw.stop();
		}

	}

	/**
	 * schedules a job to be run at one particular time in future
	 * 
	 * @param jobName
	 *            jobName for the job to be scheduled
	 * @param date
	 *            Date in <code>String</code> form
	 * @param paramsMap
	 *            List of parameters which needs to be passed when we run the
	 *            job
	 * @throws BatchException
	 */
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/scheduleonetimejob", method = RequestMethod.POST)
	@ResponseBody
	public void scheduleOneTimeJob(@RequestBody OneTimeJobScheduleParams params) {
		LOGGER.info("Request to schedule one time job for job: "
				+ params.getJobName() + "with date string" + params.getDate()
				+ " started");
		StopWatch sw = new StopWatch();
		sw.start();

		try {
			List<String> errors = batchValidator.validateOneTimeInputs(
					params.getJobName(), params.getDate());

			if (!errors.isEmpty()) {
				throw new BatchException(ApplicationErrors.BAD_REQUEST,
						errors.toString());
			}
			jobService.scheduleOneTimeJob(params);
		} catch (BatchException e) {
			LOGGER.error("Error occured while processing request to schedule one time job for job: "
					+ params.getJobName()
					+ "with date string "
					+ params.getDate());
			throw new RestException(e, e.getMessage());
		} finally {
			LOGGER.info("Request to schedule one time job for job: "
					+ params.getJobName() + " ended. Time taken (ms) = "
					+ sw.getTime());
			sw.stop();
		}

	}

	/**
	 * Update the parameter list for the job
	 * 
	 * @param jobName
	 *            for which parameters needs to be updated
	 * @param paramsMap
	 *            the <code>map</code> of parameters to be added or modified
	 */
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/updatejobproperty", method = RequestMethod.POST)
	@ResponseBody
	public void updateJobProperty(@RequestBody OneTimeJobScheduleParams params) {
		LOGGER.info("Request to update job properties for job: "
				+ params.getJobName() + " started");
		StopWatch sw = new StopWatch();
		sw.start();
		try {
			List<String> errors = batchValidator.validateUpdateInputs(params
					.getJobName());

			if (!errors.isEmpty()) {
				throw new BatchException(ApplicationErrors.BAD_REQUEST,
						errors.toString());
			}
			jobService.updateJobProperty(params.getJobName(),
					params.getParamsMap());
		} catch (BatchException e) {
			LOGGER.error("Error occured while processing request to update job properties for job: "
					+ params.getJobName());
			throw new RestException(e, e.getMessage());
		} finally {
			LOGGER.info("Request to update job properties for job: "
					+ params.getJobName() + " ended. Time taken (ms) = "
					+ sw.getTime());
			sw.stop();
		}

	}

	/**
	 * It is custom exception to be thrown
	 * 
	 * @param ex
	 * @param response
	 * @return
	 */
	@ExceptionHandler(value = { RestException.class })
	@ResponseBody
	public BatchError restExceptionHandler(RestException ex,
			HttpServletResponse response) {
		BatchError error = new BatchError();

		try {
			response.setStatus(ex.getHttpStatus().value());
			error.setErrorCode(String.valueOf(ex.getBatchException()
					.getErrorCode()));
			error.setErrorMessage(ex.getBatchException().getErrorMessage());
			error.setApplication("motech-platform-batch");

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			System.out.println("in last exception");
		}
		return error;
	}

	@RequestMapping("/sayHello")
	@ResponseBody
	public String sayHello() {
		return jobService.sayHello(); // return
												// String.format("{\"message\":\"%s\"}",
												// "Hello World");
	}
}
