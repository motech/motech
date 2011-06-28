package org.motechproject.server.pillreminder.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.model.Time;

import java.util.Date;

public class Status {
	
	private Boolean taken;
	private Date date;
	private Time windowStartTime;
	
	public Time getWindowStartTime() {
		return windowStartTime;
	}

	public void setWindowStartTime(Time windowStartTime) {
		this.windowStartTime = windowStartTime;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Boolean getTaken() {
		return taken;
	}

	public void setTaken(Boolean taken) {
		this.taken = taken;
	}
	
	@JsonIgnore
	public Date getWindowStartTimeWithDate(){
		if (windowStartTime != null && date != null) {
			return windowStartTime.getTimeOfDate(date);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return "Status [taken=" + taken + ", date=" + date
				+ ", windowStartTime=" + windowStartTime + "]";
	}
	
}
