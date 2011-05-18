package org.motechproject.pillreminder.api.model;

import java.util.UUID;

import org.motechproject.model.Time;

public class Schedule {

	private Time windowStart;
    private Time windowEnd;
    private Time startCallTime;
    private Time endCallTime;
    private Integer repeatInterval;
    private Integer repeatCount;
    private String jobId = UUID.randomUUID().toString();


    public Time getStartCallTime() {
		return startCallTime;
	}

	public void setStartCallTime(Time startCallTime) {
		this.startCallTime = startCallTime;
	}

	public Time getEndCallTime() {
		return endCallTime;
	}

	public void setEndCallTime(Time endCallTime) {
		this.endCallTime = endCallTime;
	}

	public Integer getRepeatInterval() {
		return repeatInterval;
	}

	public void setRepeatInterval(Integer repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	public Integer getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(Integer repeatCount) {
		this.repeatCount = repeatCount;
	}

	public Time getWindowStart() {
    	return windowStart;
    }
    
    public void setWindowStart(Time windowStart) {
    	this.windowStart = windowStart;
    }
    
    public Time getWindowEnd() {
    	return windowEnd;
    }
    
    public void setWindowEnd(Time windowEnd) {
    	this.windowEnd = windowEnd;
    }

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobId() {
		return jobId;
	}

}
