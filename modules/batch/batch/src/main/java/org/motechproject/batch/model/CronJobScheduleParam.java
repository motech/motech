package org.motechproject.batch.model;

import java.util.HashMap;

public class CronJobScheduleParam {
	
private String jobName;
private String cronExpression;
private HashMap<String, String> paramsMap;


public String getJobName() {
	return jobName;
}
public void setJobName(String jobName) {
	this.jobName = jobName;
}
public String getCronExpression() {
	return cronExpression;
}
public void setCronExpression(String cronExpression) {
	this.cronExpression = cronExpression;
}
public HashMap<String, String> getParamsMap() {
	return paramsMap;
}
public void setParamsMap(HashMap<String, String> paramsMap) {
	this.paramsMap = paramsMap;
}

}
