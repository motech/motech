package org.motechproject.appointmentreminder.model;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

public class Preferences extends MotechBaseDataObject {

	private static final long serialVersionUID = 7959940892352071956L;
	@TypeDiscriminator
	private String patientId;
	private int bestTimeToCall;
	private boolean enabled = false;
	

	/**
	 * @return the patientId
	 */
	public String getPatientId() {
		return patientId;
	}

	/**
	 * @param patientId the patientId to set
	 */
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	/**
	 * @return the bestTimeToCall
	 */
	public int getBestTimeToCall() {
		return bestTimeToCall;
	}
	
	/**
	 * @param bestTimeToCall the bestTimeToCall to set
	 */
	public void setBestTimeToCall(int bestTimeToCall) {
		this.bestTimeToCall = bestTimeToCall;
	}
	
	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}
	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
    @Override
    public String toString() {
        return "id = " + this.getId() + ", enabled = " + enabled + ", best time to call = " + this.bestTimeToCall + ", patient id = " + patientId; 
    }
    
    @Override
    public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    
	    Preferences p = (Preferences) o;
	    if (this.getId() != null ? !this.getId().equals(p.getId()) : p.getId() != null) return false;
	    if (this.enabled != p.isEnabled())  return false;
	    if (this.bestTimeToCall != p.getBestTimeToCall())  return false;
	    if (this.patientId != null ? !this.patientId.equals(p.getPatientId()) : p.getPatientId() != null) return false;
	    
        return true;
    }

    @Override
    public int hashCode() {
	    int result = bestTimeToCall;
	    result = 31 * result + (this.getId() != null ? this.getId().hashCode() : 0);
	    result = 31 * result + (this.patientId != null ? this.patientId.hashCode() : 0);
	    return result;
    }
	
}
