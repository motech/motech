package org.motechproject.tama.model;

import java.util.Date;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;


public class Appointment extends MotechAuditableDataObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String patientId;
	private Date reminderWindowStart;
	private Date reminderWindowEnd;
	private Date date;
	@TypeDiscriminator
	private Followup followup;

	public String getPatientId() {
		return patientId;
	}
	
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	
	public Date getReminderWindowStart() {
		return reminderWindowStart;
	}
	
	public void setReminderWindowStart(Date reminderWindowStart) {
		this.reminderWindowStart = reminderWindowStart;
	}
	
	public Date getReminderWindowEnd() {
		return reminderWindowEnd;
	}
	
	public void setReminderWindowEnd(Date reminderWindowEnd) {
		this.reminderWindowEnd = reminderWindowEnd;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Followup getFollowup() {
		return followup;
	}
	
	public void setFollowup(Followup followup) {
		this.followup = followup;
	}
	
	public static enum Followup {
		REGISTERED("Registered",7),
		WEEK4("4 week follow-up",4*7),
		WEEK12("12 week follow-up",4*12),
		WEEK24("24 week follow-up",4*24),
		WEEK36("36 week follow-up",4*36),
		WEEK48("48 week follow-up",4*48);

		final String value;
		final int days;

		private Followup(String value, int days) {
			this.value=value;
			this.days=days;
		}
		@Override
		public String toString(){
			return value;
		}
		public String getKey(){
			return name();
		}
		public int getDays() {
			return this.days;
		}
	}

}
