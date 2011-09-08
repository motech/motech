package org.motechproject.server.demo.service.impl;

import org.motechproject.server.demo.service.AuthenticationService;
import org.motechproject.tama.api.dao.PatientDAO;
import org.motechproject.tama.api.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AuthenticationServiceImpl implements AuthenticationService{
	
	@Autowired
	private PatientDAO patientDao;

	@Override
	public String getPatientIdByPhoneNumber(String phoneNumber) {
		//FIXME: remove this hack when we can support real phones
		phoneNumber = "SIP/" + phoneNumber;
		
		List<Patient> patients = patientDao.findByPhoneNumber(phoneNumber);
		if (patients != null && !patients.isEmpty()) {
			return patients.get(0).getId();
		} else {
			return null;
		}
	}

	@Override
	public boolean verifyPasscode(String patientId, String passcode) {
		Patient patient = patientDao.get(patientId);
		if (patient != null && patient.getPasscode().equals(passcode)){
			return true;
		} else {
			return false;
		}
	}

}
