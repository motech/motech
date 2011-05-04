package org.motechproject.tama.model;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

public class Doctor extends MotechAuditableDataObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@TypeDiscriminator
	private String name;
	private String clinicId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClinicId() {
		return clinicId;
	}

	public void setClinicId(String clinicId) {
		this.clinicId = clinicId;
	}

}
