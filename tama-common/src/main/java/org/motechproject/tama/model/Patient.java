package org.motechproject.tama.model;

import java.util.Date;
import java.util.Set;

import org.ektorp.docref.DocumentReferences;
import org.ektorp.docref.FetchType;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

public class Patient extends MotechAuditableDataObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@TypeDiscriminator
	private String clinicPatientId;
	private String clinicId;
	private String doctorId;
	private Gender gender;
	private String passcode;
	private String phoneNumber;
	private InterventionProgram interventionProgram = InterventionProgram.PROGRAM;
	private Date dateOfBirth;
	private Status status = Status.ACTIVE;
	@DocumentReferences(fetch = FetchType.LAZY, descendingSortOrder = true, orderBy = "windowStartDate", backReference = "patientId")
	private Set<Appointment> appointments;

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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getPasscode() {
		return passcode;
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public InterventionProgram getInterventionProgram() {
		return interventionProgram;
	}

	public void setInterventionProgram(InterventionProgram interventionProgram) {
		this.interventionProgram = interventionProgram;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Set<Appointment> getAppointments() {
		return appointments;
	}

	public void setAppointments(Set<Appointment> appointments) {
		this.appointments = appointments;
	}

	@Override
	public String toString() {
		return "Patient [id=" + getId() + ", clinicPatientId="
				+ clinicPatientId + ", clinicId=" + clinicId + "]";
	}

}
