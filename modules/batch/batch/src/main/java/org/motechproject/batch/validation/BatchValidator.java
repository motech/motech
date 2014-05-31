package org.motechproject.batch.validation;

import java.util.ArrayList;
import java.util.List;

import org.quartz.CronExpression;
import org.springframework.stereotype.Service;

@Service
public class BatchValidator {
	
	public List<String> validateShedulerInputs(String jobName , String cronExpression)
	{
		List<String> errors = new ArrayList<String>();
		if(jobName == null || ("").equals(jobName))
			{
				errors.add("Job name must be provided");
			}
		
		//boolean validCronExpress =  CronExpression.isValidExpression(cronExpression);
		if(!CronExpression.isValidExpression(cronExpression) || cronExpression == null)
			{
				errors.add("Job cron expression supplied is not valid");
			}
		return errors;
	}

	public List<String> validateOneTimeInputs(String jobName, String date) {
		
		//String datePattern = '^(([0-2]\d|[3][0-1])\/([0]\d|[1][0-2])\/[2][0]\d{2})$|^(([0-2]\d|[3][0-1])\/([0]\d|[1][0-2])\/[2][0]\d{2}\s([0-1]\d|[2][0-3])\:[0-5]\d\:[0-5]\d)$';
		List<String> errors = new ArrayList<String>();
		if(jobName == null || ("").equals(jobName))
			{
				errors.add("Job name must be provided");
			}
		//TODO validate date - use UTIL class.
		/*if(date.matches(datePattern))
		{
			errors.add("Date provided should be in ")
		}*/
		
		return errors;
	}

	public List<String> validateUpdateInputs(String jobName) {
		
		List<String> errors = new ArrayList<String>();
		if(jobName == null || ("").equals(jobName))
		{
			errors.add("Job name must be provided");
		}
		return errors;
	}

	public List<String> validateUploadInputs(String jobName, String content) {
		
		List<String> errors = new ArrayList<String>();
		if(jobName == null || ("").equals(jobName))
			{
				errors.add("Job name must be provided");
			}
		
		if(!"text/xml".equals(content))
			{
				errors.add("You must upload xml file for the job");
			}
		return errors;
	}

}
