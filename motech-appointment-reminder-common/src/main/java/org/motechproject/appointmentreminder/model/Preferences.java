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
 * “AS IS” AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
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
