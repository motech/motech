package org.motechproject.openmrs.model;

import java.io.Serializable;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Patient;

public class AppointmentReminderPreferences extends BaseOpenmrsObject implements
		Serializable {

	private static final long serialVersionUID = -2150273588332647968L;
	private Integer id;
    private Boolean moduleEnabled = null;
    private Patient patient;
    private Integer daysBefore = null;
    private Integer preferredTime = null;
    
    public AppointmentReminderPreferences() {
		super();
	}
    
    public AppointmentReminderPreferences(Boolean moduleEnabled, Patient patient, Integer daysBefore, Integer preferredTime) {
		super();

		this.moduleEnabled = moduleEnabled;
		this.patient = patient;
		this.daysBefore = daysBefore;
		this.preferredTime = preferredTime;
		
	}
    
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * The patient that these preferences are associated with.
	 * @return Associated Patient Object
	 */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Set the Patient that these preferences are associated with
     * @param patient Patient to associate
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    /**
     * The number of days before their appointment that MOTECH should being
     * reminding the patient of their appointment(s)
     * @return Reminder lead up
     */
    public Integer getDaysBefore() {
    	return daysBefore;
    }
    
    /**
     * Set the number of days before the patient's appointment that MOTECH
     * should being reminding them of their appointment(s)
     * @param daysBefore
     */
    public void setDaysBefore(Integer daysBefore) {
    	this.daysBefore = daysBefore;
    }
    
    /**
     * The perferred time for them to receive their reminder 
     * @return Preferred reminder time
     */
    public Integer getPreferredTime() {
    	return preferredTime;
    }
    
    /**
     * Set the preferred reminder time
     * @param preferredTime Preferred reminder time
     */
    public void setPreferredTime(Integer preferredTime) {
    	this.preferredTime = preferredTime;
    }
    
    /**
     * Determine if the patient wishes to make use of this module or not
     * @return TRUE = enabled/FALSE = disabled
     */
    public Boolean getModuleEnabled() {
    	return moduleEnabled;
    }
    
    /**
     * Set the user's preference in regards to enabling/disabling 
     * @param moduleParticipationStatus
     */
    public void setModuleEnabled(Boolean isEnabled) {
    	this.moduleEnabled = isEnabled;
    }

}
