package org.motechproject.server.tama.service;

/**
 * Interface to authentication service
 * @author Ricky
 */
public interface AuthenticationService {
	
	/**
	 * @param phoneNumber
	 * @return
	 */
	public String getPatientIdByPhoneNumber(String phoneNumber);

	/**
	 * Return true if the passcode matches the one in the patient recode;
	 * otherwise return false
	 * 
	 * @param phoneNumber
	 * @param passcode
	 * @return
	 */
	public boolean verifyPasscode(String patientId, String passcode);
	
}
