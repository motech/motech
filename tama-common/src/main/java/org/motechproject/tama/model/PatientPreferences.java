package org.motechproject.tama.model;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

public class PatientPreferences extends MotechAuditableDataObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@TypeDiscriminator
	private String clinicPatientId;
	private String clinicId;
	private Boolean appointmentReminderEnabled; // Is the appointment reminder
												// enabled
	private Integer bestTimeToCallHour;
	private Integer bestTimeToCallMinute;

	public String getClinicPatientId() {
		return clinicPatientId;
	}

	public void setClinicPatientId(String clinicPatientId) {
		this.clinicPatientId = clinicPatientId;
	}

	public String getClinicId() {
		return clinicId;
	}

	public void setClinicId(String clinicId) {
		this.clinicId = clinicId;
	}

	public Boolean getAppointmentReminderEnabled() {
		return appointmentReminderEnabled;
	}

	public void setAppointmentReminderEnabled(Boolean appointmentReminderEnabled) {
		this.appointmentReminderEnabled = appointmentReminderEnabled;
	}

	public Integer getBestTimeToCallHour() {
		return bestTimeToCallHour;
	}

	public void setBestTimeToCallHour(Integer bestTimeToCallHour) {
		this.bestTimeToCallHour = bestTimeToCallHour;
	}

	public Integer getBestTimeToCallMinute() {
		return bestTimeToCallMinute;
	}

	public void setBestTimeToCallMinute(Integer bestTimeToCallMinute) {
		this.bestTimeToCallMinute = bestTimeToCallMinute;
	}

}
