package org.motechproject.batch.model;

import java.util.HashMap;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include = Inclusion.NON_NULL)
public class OneTimeJobScheduleParams {

	private String jobName;
	private String date;
	private HashMap<String, String> paramsMap;
	
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public HashMap<String, String> getParamsMap() {
		return paramsMap;
	}
	public void setParamsMap(HashMap<String, String> paramsMap) {
		this.paramsMap = paramsMap;
	}
	
}
