/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.appointmentreminder.model;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

public class Preferences extends MotechBaseDataObject {

	private static final long serialVersionUID = 7959940892352071956L;
	@TypeDiscriminator
	private String patientId;
	private Integer bestTimeToCallHour;
	private Integer bestTimeToCallMinute;
	private Boolean enabled = Boolean.FALSE;
	private String ivrCallJobId;

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
	
	/**
	 * @return the enabled
	 */
	public Boolean isEnabled() {
		return enabled;
	}
	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
    public String getIvrCallJobId() {
		return ivrCallJobId;
	}

	public void setIvrCallJobId(String ivrCallJobId) {
		this.ivrCallJobId = ivrCallJobId;
	}

	@Override
    public String toString() {
        return "id = " + this.getId() + ", enabled = " + enabled + ", best time to call hour = " + this.bestTimeToCallHour + ", best time to call minute = " + this.bestTimeToCallMinute + ", patient id = " + patientId; 
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((bestTimeToCallHour == null) ? 0 : bestTimeToCallHour
						.hashCode());
		result = prime
				* result
				+ ((bestTimeToCallMinute == null) ? 0 : bestTimeToCallMinute
						.hashCode());
		result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
		result = prime * result
				+ ((patientId == null) ? 0 : patientId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Preferences other = (Preferences) obj;
		if (bestTimeToCallHour == null) {
			if (other.bestTimeToCallHour != null)
				return false;
		} else if (!bestTimeToCallHour.equals(other.bestTimeToCallHour))
			return false;
		if (bestTimeToCallMinute == null) {
			if (other.bestTimeToCallMinute != null)
				return false;
		} else if (!bestTimeToCallMinute.equals(other.bestTimeToCallMinute))
			return false;
		if (enabled == null) {
			if (other.enabled != null)
				return false;
		} else if (!enabled.equals(other.enabled))
			return false;
		if (patientId == null) {
			if (other.patientId != null)
				return false;
		} else if (!patientId.equals(other.patientId))
			return false;
		return true;
	}
    
}
