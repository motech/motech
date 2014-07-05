package org.motechproject.batch.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.batch.operations.JobOperator;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.JobStartException;

import org.apache.log4j.Logger;
import org.motechproject.batch.exception.ApplicationErrors;
import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.mds.BatchJob;
import org.motechproject.batch.mds.BatchJobExecution;
import org.motechproject.batch.mds.BatchJobParameters;
import org.motechproject.batch.mds.service.BatchJobMDSService;
import org.motechproject.batch.mds.service.BatchJobParameterMDSService;
import org.motechproject.batch.model.JobExecutionHistoryDTO;
import org.motechproject.batch.model.JobExecutionHistoryList;
import org.motechproject.batch.service.JobTriggerService;
import org.motechproject.batch.util.BatchConstants;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Class to perform the trigger operation for all types of jobs
 * 
 * @author Naveen
 * 
 */
@Service(value = "jobTriggerService")
public class JobTriggerServiceImpl implements JobTriggerService {

    private static final Logger LOGGER = Logger
            .getLogger(JobTriggerServiceImpl.class);

    private JobOperator jsrJobOperator;

    @Autowired
    public JobTriggerServiceImpl(BatchJobMDSService jobRepo,
            BatchJobParameterMDSService jobParameterRepo,
            JobOperator jsrJobOperator) {
        this.jobRepo = jobRepo;
        this.jobParameterRepo = jobParameterRepo;
        this.jsrJobOperator = jsrJobOperator;

    }

    private BatchJobParameterMDSService jobParameterRepo;

    private BatchJobMDSService jobRepo;

    @Override
    @MotechListener(subjects = BatchConstants.EVENT_SUBJECT)
    public void handleEvent(MotechEvent event) throws BatchException {
        String jobName = event.getParameters().get(BatchConstants.JOB_NAME_KEY)
                .toString();
        triggerJob(jobName);
    }

    @Override
    public void triggerJob(String jobName) throws BatchException {
        LOGGER.info("Starting executing JOB: " + jobName);
        ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        ClassLoader contextClassLoader = getClass().getClassLoader();
        BatchJobClassLoader testLoader = new BatchJobClassLoader(
                contextClassLoader);

        Thread.currentThread().setContextClassLoader(testLoader);
        List<BatchJob> batchJobList = jobRepo.findByJobName(jobName);
        boolean jobExists = true;
        if (batchJobList == null || batchJobList.size() == 0) {
            jobExists = false;
        }
        if (!jobExists) {
            throw new BatchException(ApplicationErrors.JOB_NOT_FOUND);
        }
        BatchJob batchJob = batchJobList.get(0);
        long batchJobId = (long) jobRepo.getDetachedField(batchJob, "id");

        List<BatchJobParameters> parametersList = jobParameterRepo
                .findByJobId((int) batchJobId);

        Properties jobParameters = new Properties();

        for (BatchJobParameters batchJobParameter : parametersList) {
            jobParameters.put(batchJobParameter.getParameterName(),
                    batchJobParameter.getParameterValue());
        }

        try {
            jsrJobOperator.start(jobName, jobParameters);
        } catch (JobStartException | JobSecurityException e) {

            throw new BatchException(ApplicationErrors.JOB_TRIGGER_FAILED, e,
                    ApplicationErrors.JOB_TRIGGER_FAILED.getMessage());
        }

        Thread.currentThread().setContextClassLoader(classLoader);
        // TODO Implement the datetime

    }

    @Override
    public JobExecutionHistoryList getJObExecutionHistory(String jobName)
            throws BatchException {

        List<BatchJob> batchJobList = jobRepo.findByJobName(jobName);
        boolean jobExists = true;
        if (batchJobList == null || batchJobList.size() == 0) {
            jobExists = false;
        }
        if (!jobExists) {
            throw new BatchException(ApplicationErrors.JOB_NOT_FOUND);
        }
        List<BatchJobExecution> executionHistoryList = null;
        JobExecutionHistoryList jobExecutionHistoryListDto = new JobExecutionHistoryList();

        List<JobExecutionHistoryDTO> jobExecutionHistoryList = new ArrayList<JobExecutionHistoryDTO>();

        for (BatchJobExecution executionJob : executionHistoryList) {
            JobExecutionHistoryDTO executionHistoryDTO = new JobExecutionHistoryDTO();

            executionHistoryDTO.setJobExecutionId(executionJob
                    .getJobExecutionId());
            executionHistoryDTO.setVersion(executionJob.getVersion());

            executionHistoryDTO.setStartTime(executionJob.getStartTime());
            executionHistoryDTO.setEndTime(executionJob.getEndTime());
            executionHistoryDTO.setStatus(executionJob.getStatus());
            executionHistoryDTO.setExitCode(executionJob.getExitCode());
            executionHistoryDTO.setExitMessage(executionJob.getExitMessage());
            executionHistoryDTO.setLastUpdated(executionJob.getLastUpdated());

            executionHistoryDTO.setCreatedBy(executionJob.getCreatedBy());
            executionHistoryDTO.setLastUpdatedBy(executionJob
                    .getLastUpdatedBy());

            jobExecutionHistoryList.add(executionHistoryDTO);

        }
        jobExecutionHistoryListDto
                .setJobExecutionHistoryList(executionHistoryList);

        return jobExecutionHistoryListDto;
    }

}
