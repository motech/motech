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
package org.motechproject.tama.api.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Years;
import org.motechproject.model.MotechAuditableDataObject;

import java.util.Date;

@TypeDiscriminator("doc.type === 'PATIENT'")
public class Patient extends MotechAuditableDataObject {

	private static final long serialVersionUID = -4678392647206490010L;

    public enum Status {
	    ACTIVE, INACTIVE
    }

    public enum InterventionProgram {
	    //Hardcoded a program for now
	    PROGRAM
    }

    public enum Gender {
    	MALE("Male"), FEMALE("Female"), HIJIRA("Hijira");
        private String text;

        Gender(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public static Gender fromString(String text) {
            if (text != null) {
                for (Gender b : Gender.values()) {
                    if (text.equalsIgnoreCase(b.text)) {
                      return b;
                    }
                }
            }
            return null;
        }
    }

    public enum Regimen {
	    REGIMEN_1, REGIMEN_2
    }

	private String clinicPatientId;
	private Gender gender;
	private String clinicId;
	private String doctorId;
	private String phoneNumber;
	private Preferences preferences;
	private String passcode;
	private InterventionProgram interventionProgram = InterventionProgram.PROGRAM;
	private Date dateOfBirth;
	private Status status = Status.ACTIVE;
    private Date registrationDate;
    private Regimen regimen;

    @JsonProperty("type") private final String type = "PATIENT";

	/**
	 * @return the clinicPatientId
	 */
	public String getClinicPatientId() {
		return clinicPatientId;
	}
	/**
	 * @param clinicPatientId the clinicPatientId to set
	 */
	public void setClinicPatientId(String clinicPatientId) {
		this.clinicPatientId = clinicPatientId;
	}
	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	/**
	 * @return the gender
	 */
	public Gender getGender() {
		return gender;
	}
	/**
	 * @param gender the gender to set
	 */
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	/**
	 * @return the clinic
	 */
	public String getClinicId() {
		return clinicId;
	}
	/**
	 * @param clinicId the clinic to set
	 */
	public void setClinicId(String clinicId) {
		this.clinicId = clinicId;
	}
	/**
	 * @return the doctor
	 */
	public String getDoctorId() {
		return doctorId;
	}
	/**
	 * @param doctorId the doctor to set
	 */
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	
	/**
	 * @return the preferences
	 */
	public Preferences getPreferences() {
		return preferences;
	}
	/**
	 * @param preferences the preferences to set
	 */
	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

    public String getPasscode()
    {
        return passcode;
    }

    public void setPasscode(String passcode)
    {
        this.passcode = passcode;
    }

    public InterventionProgram getInterventionProgram()
    {
        return interventionProgram;
    }

    public void setInterventionProgram(InterventionProgram interventionProgram)
    {
        this.interventionProgram = interventionProgram;
    }

    public Date getDateOfBirth()
    {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth)
    {
        this.dateOfBirth = dateOfBirth;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Regimen getRegimen() {
        return regimen;
    }

    public void setRegimen(Regimen regimen) {
        this.regimen = regimen;
    }

    @JsonIgnore
	public Integer getAge(){
		if (dateOfBirth != null) {
			return Years.yearsBetween(new DateTime(dateOfBirth), new DateTime()).getYears();
		} else {
			return null;
		}
	}

	@JsonIgnore
	public Integer getDaysSinceRegistered(){
		if (registrationDate != null) {
			return Days.daysBetween(new DateTime(registrationDate), new DateTime()).getDays();
		} else {
			return null;
		}
	}

    @Override
    public String toString() {
        return "id = " + this.getId() + ", clinic patient id = " + clinicPatientId + ", gender = " + this.gender + ", phone number = " + phoneNumber + "preferences = {" + ((this.preferences != null) ? this.preferences.toString() : "null") + ", clinic = {" + ((this.clinicId != null) ? this.clinicId.toString() : "null") + "}, doctor = {" + ((this.doctorId != null) ? doctorId.toString() : "null") + "}";
    }
    
    @Override
    public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    
	    Patient p = (Patient) o;
	    if (this.getId() != null ? !this.getId().equals(p.getId()) : p.getId() != null) return false;
	    if (this.clinicPatientId != null ? !this.clinicPatientId.equals(p.getClinicPatientId()) : p.getClinicPatientId() != null) return false;
	    if (this.clinicId != null ? !this.clinicId.equals(p.getClinicId()) : p.getClinicId() != null) return false;
	    if (this.doctorId != null ? !this.doctorId.equals(p.getDoctorId()) : p.getDoctorId() != null) return false;
	    if (this.phoneNumber != null ? !this.phoneNumber.equals(p.getPhoneNumber()) : p.getPhoneNumber() != null) return false;
	    
        return true;
    }

    @Override
    public int hashCode() {
	    int result = this.getId() != null ? this.getId().hashCode() : 0;
	    result = 31 * result + (this.clinicPatientId != null ? this.clinicPatientId.hashCode() : 0);
	    result = 31 * result + (this.clinicId != null ? this.clinicId.hashCode() : 0);
	    result = 31 * result + (this.gender != null ? this.gender.hashCode() : 0);
	    result = 31 * result + (this.doctorId != null ? this.doctorId.hashCode() : 0);
	    result = 31 * result + (this.phoneNumber != null ? this.phoneNumber.hashCode() : 0);
	    return result;
    }
	
}
