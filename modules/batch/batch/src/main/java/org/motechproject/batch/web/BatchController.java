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

    private static final Logger LOGGER = Logger
            .getLogger(BatchController.class);

    @Autowired
    private JobService jobService;

    @Autowired
    private FileUploadService fileUploadService;

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
    private JobTriggerService jobTriggerService;

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
            LOGGER.info(String
                    .format("Request to get list of batch jobs ended. Time taken (ms) = %d",
                            sw.getTime()));
            sw.stop();
        }

    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/jobHistory/{jobName}", method = RequestMethod.GET)
    @ResponseBody
    public JobExecutionHistoryList getjobHistoryList(
            @PathVariable("jobName") String jobName) {
        LOGGER.info(String.format(
                "Request to get execution history of job %s started", jobName));
        StopWatch sw = new StopWatch();
        sw.start();
        JobExecutionHistoryList jobExecutionHistoryList = null;
        try {
            List<String> errors = batchValidator.validateUpdateInputs(jobName);
            if (!errors.isEmpty()) {
                LOGGER.error("Error occured while processing request to get job history");
                throw new BatchException(ApplicationErrors.BAD_REQUEST,
                        errors.toString());
            }
            jobExecutionHistoryList = jobTriggerService
                    .getJObExecutionHistory(jobName);
            return jobExecutionHistoryList;
        } catch (BatchException e) {
            LOGGER.error(String
                    .format("Error occured while processing request to get execution history for job %s",
                            jobName));
            throw new RestException(e, e.getMessage());
        } finally {
            LOGGER.info(String
                    .format("Request to get execution history for job %s ended. Time taken (ms) = %d",
                            jobName, sw.getTime()));
            sw.stop();
        }

    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/trigger/{jobName}", method = RequestMethod.GET)
    @ResponseBody
    public void triggerJob(@PathVariable("jobName") String jobName) {

        LOGGER.info(String.format("Request to trigger the job %s started",
                jobName));
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
            LOGGER.error(String.format(
                    "Error occured while processing request to trigger job %s",
                    jobName));
            throw new RestException(e, e.getMessage());
        } finally {
            LOGGER.info(String
                    .format("Request to trigger the job %s ended. Time taken (ms) = %d ",
                            jobName + sw.getTime()));
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
    @RequestMapping(value = "/schedulecronjob", method = RequestMethod.POST)
    @ResponseBody
    public void scheduleCronJob(CronJobScheduleParam params,
            @RequestParam("file") MultipartFile file) {

        LOGGER.info(String
                .format("Request to schedule a cron job for job %s with cron expression %s started",
                        params.getJobName(), params.getCronExpression()));
        StopWatch sw = new StopWatch();
        sw.start();
        try {
            List<String> errors = batchValidator.validateShedulerInputs(
                    params.getJobName(), params.getCronExpression());
            errors.addAll(batchValidator.validateUploadInputs(
                    params.getJobName(), file.getContentType()));

            if (!errors.isEmpty()) {
                throw new BatchException(ApplicationErrors.BAD_REQUEST,
                        errors.toString());
            }
            fileUploadService.uploadFile(params.getJobName(), file, xmlPath);
            jobService.scheduleJob(params);
        } catch (BatchException e) {
            LOGGER.error(String
                    .format("Error occured while processing request to schedule a cron job for job %s with cron expression %s",
                            params.getJobName(), params.getCronExpression()));
            throw new RestException(e, e.getMessage());
        } finally {
            LOGGER.info(String
                    .format("Request to schedule a cron job for job %s ended. Time taken (ms) = %d",
                            params.getJobName(), sw.getTime()));
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
        LOGGER.info(String
                .format("Request to schedule one time job for job %s with date %s started",
                        params.getJobName(), params.getDate()));
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
            LOGGER.error(String
                    .format("Error occured while processing request to schedule one time job for job %s with date %s started",
                            params.getJobName(), params.getDate()));
            throw new RestException(e, e.getMessage());
        } finally {
            LOGGER.info(String
                    .format("Request to schedule one time job for job %s ended. Time taken (ms) = %d",
                            params.getJobName(), sw.getTime()));
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
        LOGGER.info(String.format(
                "Request to update job properties for job %s started",
                params.getJobName()));
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
            LOGGER.error(String
                    .format("Error occured while processing request to update job properties for job %s",
                            params.getJobName()));
            throw new RestException(e, e.getMessage());
        } finally {
            LOGGER.info(String
                    .format("Request to update job properties for job %s ended. Time taken (ms) = %d",
                            params.getJobName(), sw.getTime()));
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
        }
        return error;
    }

    /**
     * Ping method to test if application is up. To be removed
     * 
     * @return
     */
    @RequestMapping("/sayHello")
    @ResponseBody
    public String sayHello() {
        return jobService.sayHello();

    }
}
