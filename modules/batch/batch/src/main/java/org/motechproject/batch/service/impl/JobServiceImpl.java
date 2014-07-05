package org.motechproject.batch.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.batch.exception.ApplicationErrors;
import org.motechproject.batch.exception.BatchException;
import org.motechproject.batch.mds.BatchJob;
import org.motechproject.batch.mds.BatchJobParameters;
import org.motechproject.batch.mds.service.BatchJobMDSService;
import org.motechproject.batch.mds.service.BatchJobParameterMDSService;
import org.motechproject.batch.model.BatchJobDTO;
import org.motechproject.batch.model.BatchJobListDTO;
import org.motechproject.batch.model.CronJobScheduleParam;
import org.motechproject.batch.model.JobStatusLookup;
import org.motechproject.batch.model.OneTimeJobScheduleParams;
import org.motechproject.batch.service.JobService;
import org.motechproject.batch.util.BatchConstants;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.CronSchedulableJob;
import org.motechproject.scheduler.contract.RunOnceSchedulableJob;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class to schedule reschedule jobs or update job parameters
 * @author Naveen
 *
 */

@Service(value="jobService")
@Transactional
public class JobServiceImpl implements JobService {
	
	//@Autowired
	//private BatchJobMDSService batchJobMDSService;
//	@Autowired
//	private JobRepository jobRepo;
//	@Autowired
//	private JobStatusRepository jobStatusRepo;
//	@Autowired
//	private JobParametersRepository jobParameterRepo;
//	
//	
//	public JobParametersRepository getJobParameterRepo() {
//		return jobParameterRepo;
//	}
//
//	public void setJobParameterRepo(JobParametersRepository jobParameterRepo) {
//		this.jobParameterRepo = jobParameterRepo;
//	}

	private BatchJobMDSService jobRepo;
	
	private BatchJobParameterMDSService  jobParameterRepo;
	@Autowired
	private MotechSchedulerService schedulerService;
	
  
	@Autowired
	public JobServiceImpl(BatchJobMDSService jobRepo,
			BatchJobParameterMDSService jobParameterRepo, MotechSchedulerService schedulerService) {
		this.jobRepo = jobRepo;
		this.jobParameterRepo = jobParameterRepo;
		this.schedulerService = schedulerService;

	}

	public JobServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public BatchJobListDTO getListOfJobs() throws BatchException{
		BatchJobListDTO listDto = new BatchJobListDTO();
		List<BatchJob> jobList = jobRepo.retrieveAll();
		List<BatchJobDTO> jobDtoList = null;
		if(jobList!=null)
		{
			jobDtoList = new ArrayList<BatchJobDTO>();
		for(BatchJob batchJob : jobList)
		{
			
			BatchJobDTO batchJobDto = new BatchJobDTO();
			Object id = jobRepo.getDetachedField(batchJob, "id");
			Object modDate = jobRepo.getDetachedField(batchJob, "modificationDate");
			Object creationDate = jobRepo.getDetachedField(batchJob, "creationDate");
			Long l =  Long.parseLong(String.valueOf(id));;
			
			batchJobDto.setJobId(l);
			batchJobDto.setJobName(batchJob.getJobName());
			batchJobDto.setCronExpression(batchJob.getCronExpression());
			//TODO check whether the fields are coming in the right format
			System.out.println(creationDate.toString());
			batchJobDto.setCreateTime(new DateTime(creationDate.toString()));
			batchJobDto.setLastUpdated(new DateTime(modDate.toString()));
			jobDtoList.add(batchJobDto);
			
		}
		}
		
		listDto.setBatchJobDtoList(jobDtoList);
//		
		return listDto;
	}

	

	@Override
	public void scheduleJob(CronJobScheduleParam params) throws BatchException{
			
			//BatchJobStatus batchJobStatus = jobStatusRepo.getActiveObject(BatchConstants.ACTIVE_STATUS);
	   
			BatchJob batchJob = new BatchJob();
			//batchJob.setJobId(jobRepo.getNextKey());
			batchJob.setCronExpression(params.getCronExpression());
			batchJob.setJobName(params.getJobName());
			batchJob.setBatchJobStatusId(JobStatusLookup.ACTIVE.getId());
			
		    jobRepo.create(batchJob);
		    int batchId = Integer.parseInt(String.valueOf(jobRepo.getDetachedField(batchJob, "id")));
		    for(String key : params.getParamsMap().keySet())
		    	{
		    		
				    BatchJobParameters batchJobParms = new BatchJobParameters();
				    batchJobParms.setBatchJobId(batchId);
				    batchJobParms.setParameterName(key);
				    batchJobParms.setParameterValue(params.getParamsMap().get(key));
				    
				    jobParameterRepo.create(batchJobParms);
		    	}
		    HashMap<String, Object> parameters = new HashMap<>();
		    parameters.put(MotechSchedulerService.JOB_ID_KEY, String.format("%s_%s", "BATCH", batchJob.getJobName()));
		    parameters.put(BatchConstants.JOB_NAME_KEY, batchJob.getJobName());
		    MotechEvent motechEvent = new MotechEvent(BatchConstants.EVENT_SUBJECT, parameters);
		    CronSchedulableJob providerSyncCronJob = new CronSchedulableJob(motechEvent, batchJob.getCronExpression());
	        schedulerService.scheduleJob(providerSyncCronJob);	
	}

	@Override
	public void scheduleOneTimeJob(OneTimeJobScheduleParams params) throws BatchException {
	
			DateTimeFormatter formatter = DateTimeFormat.forPattern(BatchConstants.DATE_FORMAT);
			DateTime dt = formatter.parseDateTime(params.getDate());
			DateTime now = new DateTime();
			if (dt.isBefore(now)){
				throw new  BatchException(ApplicationErrors.BAD_REQUEST, String.format("Date[%s] is in past. Past date is not allowed", params.getDate()));
			}
			String cronString = getCronString(dt);
			//BatchJobStatus batchJobStatus = jobStatusRepo.getActiveObject(BatchConstants.ACTIVE_STATUS);
			
			BatchJob batchJob = new BatchJob();
			batchJob.setCronExpression(cronString);
			batchJob.setJobName(params.getJobName());
			batchJob.setBatchJobStatusId(JobStatusLookup.ACTIVE.getId());
			
		    jobRepo.create(batchJob);
		    long batchId = (long)jobRepo.getDetachedField(batchJob, "id");
		    
		    for(String key : params.getParamsMap().keySet())
		    	{
		    		
				    BatchJobParameters batchJobParms = new BatchJobParameters();
				    batchJobParms.setBatchJobId((int)batchId);
				    batchJobParms.setParameterName(key);
				    batchJobParms.setParameterValue(params.getParamsMap().get(key));
				    
				    jobParameterRepo.create(batchJobParms);
		    	}
		    HashMap<String, Object> parameters = new HashMap<>();
		    parameters.put(MotechSchedulerService.JOB_ID_KEY, String.format("%s_%s", "BATCH", batchJob.getJobName()));
		    parameters.put(BatchConstants.JOB_NAME_KEY, batchJob.getJobName());
		    MotechEvent motechEvent = new MotechEvent(BatchConstants.EVENT_SUBJECT, parameters);
		    RunOnceSchedulableJob schedulableJob = null;
			try {
				schedulableJob = new RunOnceSchedulableJob(motechEvent, new SimpleDateFormat(BatchConstants.DATE_FORMAT, Locale.ENGLISH).parse(params.getDate()));
			} catch (ParseException e) {
				throw new BatchException(ApplicationErrors.BAD_REQUEST, String.format("Date[%s] not in correct format. Correct format is [%s]", params.getDate(), BatchConstants.DATE_FORMAT));
			}
	        schedulerService.scheduleRunOnceJob(schedulableJob);
		}
	
	@SuppressWarnings("deprecation")
	@Override
	public void updateJobProperty(String jobName, HashMap<String, String> paramsMap) throws BatchException
		{
			List<BatchJob> batchJobList = jobRepo.findByJobName(jobName);
			if(batchJobList == null || batchJobList.isEmpty()) {
				throw new BatchException(ApplicationErrors.JOB_NOT_FOUND);
			}
			if(batchJobList.size() > 1) {
				throw new BatchException(ApplicationErrors.DUPLICATE_JOB);
			}
			
			BatchJob batchJob = batchJobList.get(0);
			int batchJobId = (int)(long)jobRepo.getDetachedField(batchJob, "id");
			List<BatchJobParameters> batchJobParametersList = 
					jobParameterRepo.findByJobId(batchJobId);
			List<String> keyList = new ArrayList<String>();
			for(BatchJobParameters jobParam : batchJobParametersList)
			{
				keyList.add(jobParam.getParameterName());
			}
		for(String key : paramsMap.keySet())
    	{
    		if(keyList.contains(key))
    		{
    			int index = keyList.indexOf(key);
    			BatchJobParameters batchJobParam = batchJobParametersList.get(index);
    			batchJobParam.setParameterValue(paramsMap.get(key));
    			jobParameterRepo.update(batchJobParam);
    		}
    		
    		else
    		{
    			BatchJobParameters batchJobParms = new BatchJobParameters();
    		    batchJobParms.setBatchJobId(batchJobId);
    		    batchJobParms.setParameterName(key);
    		    batchJobParms.setParameterValue(paramsMap.get(key));
    		    
    		    jobParameterRepo.create(batchJobParms);

    		}
		    HashMap<String, Object> parameters = new HashMap<>();
		    parameters.put(MotechSchedulerService.JOB_ID_KEY, String.format("%s_%s", "BATCH", batchJob.getJobName()));
		    parameters.put(BatchConstants.JOB_NAME_KEY, batchJob.getJobName());
		    MotechEvent motechEvent = new MotechEvent(BatchConstants.EVENT_SUBJECT, parameters);
	        schedulerService.updateScheduledJob(motechEvent);
		 }
		}
	
	/**
	 * returns a cron string generated from datetime parameter in the form <code>dd/MM/yyyy HH:mm:ss</code>
	 * @param date date parameter from which cron string mwill be generated
	 * @return <code>String</code> representing cron expression
	 */
	private String getCronString(DateTime date)
	{
		String cronString = "00"+" "+date.getMinuteOfHour()+" "+date.getHourOfDay()+" "+date.getDayOfMonth()+" "+date.getMonthOfYear()+" ? "+date.getYear();
		return cronString;
	}

	@Override
	public String sayHello() {
		 BatchJob batchJob = new BatchJob(1,"batch-test");
		 jobRepo.create(batchJob);
		 List<BatchJob> hubTopics = jobRepo.retrieveAll();
		 
	      return String.format("{\"message\":\"%s\"}", "Hello World " + hubTopics.size());

	
	}
	

}
