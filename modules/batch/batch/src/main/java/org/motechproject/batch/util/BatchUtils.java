package org.motechproject.batch.util;

import org.motechproject.batch.exception.BatchException;

import org.springframework.beans.factory.annotation.Autowired;

public class BatchUtils {
	
	//@Autowired
//	static
//	JobRepository jobRepo;
//	
	

/**
 * Checks if any job with given name has been scheduled
 * @param jobName job name of the job to queried
 * @return boolean yes if job exists or no in other case
 * @throws BatchException
 */
	/*public static boolean checkJobName(String jobName) throws BatchException {
		BatchJob batchJob = jobRepo.getBatchJob(jobName);
		boolean flag = true;
		
		if(batchJob == null)
			flag = false;
		
		return flag;
	}*/

}
