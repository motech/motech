package org.motechproject.tama.model;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

public class Clinic extends MotechAuditableDataObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@TypeDiscriminator
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
