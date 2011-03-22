/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
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
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.motechproject.model;

import org.ektorp.docref.DocumentReferences;
import org.ektorp.docref.FetchType;
import org.ektorp.support.TypeDiscriminator;

import java.util.Date;
import java.util.Set;

/**
 *
 *
 */
public class Patient extends MotechAuditableDataObject {

    private static final long serialVersionUID = 1L;

    @TypeDiscriminator
    private String clinicPatientId;
    private long passCode;
    private String phoneNumber;
    private String status;

    private String preferredLanguage;
    private int bestCallTime;
    private String dailyCallScheduledJobId;
    private boolean sendAppointmentReminder;

    private Date nextAppointmentDueDate;
    private Date nextAppointmentDate;

    @DocumentReferences(fetch = FetchType.LAZY, descendingSortOrder = true, orderBy = "appointmentDateTime", backReference = "patientId")
    private Set<Visit> visits;

    public String getClinicPatientId() {
        return clinicPatientId;
    }

    public void setClinicPatientId(String clinicPatientId) {
        this.clinicPatientId = clinicPatientId;
    }

    public long getPassCode() {
        return passCode;
    }

    public void setPassCode(long passCode) {
        this.passCode = passCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public int getBestCallTime() {
        return bestCallTime;
    }

    public void setBestCallTime(int bestCallTime) {
        this.bestCallTime = bestCallTime;
    }

    public String getDailyCallScheduledJobId() {
        return dailyCallScheduledJobId;
    }

    public void setDailyCallScheduledJobId(String dailyCallScheduledJobId) {
        this.dailyCallScheduledJobId = dailyCallScheduledJobId;
    }

    public boolean isSendAppointmentReminder() {
        return sendAppointmentReminder;
    }

    public void setSendAppointmentReminder(boolean sendAppointmentReminder) {
        this.sendAppointmentReminder = sendAppointmentReminder;
    }

    public Date getNextAppointmentDueDate() {
        return nextAppointmentDueDate;
    }

    public void setNextAppointmentDueDate(Date nextAppointmentDueDate) {
        this.nextAppointmentDueDate = nextAppointmentDueDate;
    }

    public Date getNextAppointmentDate() {
        return nextAppointmentDate;
    }

    public void setNextAppointmentDate(Date nextAppointmentDate) {
        this.nextAppointmentDate = nextAppointmentDate;
    }

    public Set<Visit> getVisits() {
        return visits;
    }

    public void setVisits(Set<Visit> visits) {
        this.visits = visits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Patient patient = (Patient) o;

        if (bestCallTime != patient.bestCallTime) return false;
        if (passCode != patient.passCode) return false;
        if (sendAppointmentReminder != patient.sendAppointmentReminder) return false;
        if (clinicPatientId != null ? !clinicPatientId.equals(patient.clinicPatientId) : patient.clinicPatientId != null)
            return false;
        if (dailyCallScheduledJobId != null ? !dailyCallScheduledJobId.equals(patient.dailyCallScheduledJobId) : patient.dailyCallScheduledJobId != null)
            return false;
        if (nextAppointmentDate != null ? !nextAppointmentDate.equals(patient.nextAppointmentDate) : patient.nextAppointmentDate != null)
            return false;
        if (nextAppointmentDueDate != null ? !nextAppointmentDueDate.equals(patient.nextAppointmentDueDate) : patient.nextAppointmentDueDate != null)
            return false;
        if (phoneNumber != null ? !phoneNumber.equals(patient.phoneNumber) : patient.phoneNumber != null) return false;
        if (preferredLanguage != null ? !preferredLanguage.equals(patient.preferredLanguage) : patient.preferredLanguage != null)
            return false;
        if (status != null ? !status.equals(patient.status) : patient.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clinicPatientId != null ? clinicPatientId.hashCode() : 0;
        result = 31 * result + (int) (passCode ^ (passCode >>> 32));
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (preferredLanguage != null ? preferredLanguage.hashCode() : 0);
        result = 31 * result + bestCallTime;
        result = 31 * result + (dailyCallScheduledJobId != null ? dailyCallScheduledJobId.hashCode() : 0);
        result = 31 * result + (sendAppointmentReminder ? 1 : 0);
        result = 31 * result + (nextAppointmentDueDate != null ? nextAppointmentDueDate.hashCode() : 0);
        result = 31 * result + (nextAppointmentDate != null ? nextAppointmentDate.hashCode() : 0);
        return result;
    }
}
