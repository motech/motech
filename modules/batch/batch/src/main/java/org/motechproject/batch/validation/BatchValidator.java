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

}
