package org.motechproject.pillreminder.api.model;

import java.util.Date;

import org.motechproject.model.Time;

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

}
