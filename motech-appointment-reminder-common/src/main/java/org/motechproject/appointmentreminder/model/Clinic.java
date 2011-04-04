package org.motechproject.appointmentreminder.model;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

public class Clinic extends MotechAuditableDataObject {

	private static final long serialVersionUID = 8466662959316007760L;
	@TypeDiscriminator
	private String name;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
    @Override
    public String toString() {
        return "id = " + this.getId() + ", name = " + name; 
    }
    
    @Override
    public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    
	    Clinic d = (Clinic) o;
	    if (this.getId() != null ? !this.getId().equals(d.getId()) : d.getId() != null) return false;
	    if (this.name != null ? !this.name.equals(d.getName()) : d.getName() != null) return false;
	    
        return true;
    }

    @Override
    public int hashCode() {
	    int result = this.getId() != null ? this.getId().hashCode() : 0;
	    result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
	    return result;
    }
}
