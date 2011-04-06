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
package org.motechproject.server.appointmentreminder.model;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

import java.util.Date;

/**
 *
 */
public class Appointment extends MotechAuditableDataObject {

     private static final long serialVersionUID = 1L;

    private String patientId;
    @TypeDiscriminator
    private Boolean patientArrived = false;
    private Date arrivalDate;
    private Date windowStartDate;
    private Date windowEndDate;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Boolean getPatientArrived() {
        return patientArrived;
    }

    public void setPatientArrived(Boolean patientArrived) {
        this.patientArrived = patientArrived;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Date getWindowStartDate() {
        return windowStartDate;
    }

    public void setWindowStartDate(Date windowStartDate) {
        this.windowStartDate = windowStartDate;
    }

    public Date getWindowEndDate() {
        return windowEndDate;
    }

    public void setWindowEndDate(Date windowEndDate) {
        this.windowEndDate = windowEndDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Appointment that = (Appointment) o;

        if (arrivalDate != null ? !arrivalDate.equals(that.arrivalDate) : that.arrivalDate != null) return false;
        if (patientArrived != null ? !patientArrived.equals(that.patientArrived) : that.patientArrived != null)
            return false;
        if (patientId != null ? !patientId.equals(that.patientId) : that.patientId != null) return false;
        if (windowEndDate != null ? !windowEndDate.equals(that.windowEndDate) : that.windowEndDate != null)
            return false;
        if (windowStartDate != null ? !windowStartDate.equals(that.windowStartDate) : that.windowStartDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = patientId != null ? patientId.hashCode() : 0;
        result = 31 * result + (patientArrived != null ? patientArrived.hashCode() : 0);
        result = 31 * result + (arrivalDate != null ? arrivalDate.hashCode() : 0);
        result = 31 * result + (windowStartDate != null ? windowStartDate.hashCode() : 0);
        result = 31 * result + (windowEndDate != null ? windowEndDate.hashCode() : 0);
        return result;
    }
}
