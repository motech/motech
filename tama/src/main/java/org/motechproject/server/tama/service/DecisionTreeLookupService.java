package org.motechproject.server.tama.service;

import org.motechproject.tama.model.Patient;

/**
 * 
 * @author Ricky
 */
public interface DecisionTreeLookupService {

	/**
	 * Retrieve the name of the decision tree which matches the given patient
	 * 
	 * @param patient
	 * @return (Tree.name)
	 */
	public String findTreeNameByPatient(Patient patient);
	
}
