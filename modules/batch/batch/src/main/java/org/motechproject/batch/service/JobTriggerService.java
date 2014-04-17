package org.motechproject.batch.service;

import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public interface JobTriggerService {
	void triggerJob(String jobName , Date date);
	

}
