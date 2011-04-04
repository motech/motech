package org.motechproject.appointmentreminder.model;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

public class Doctor extends MotechBaseDataObject {
	
	private static final long serialVersionUID = 6716142269089685993L;
	@TypeDiscriminator
	private String name;
	private Clinic clinic;

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

	/**
	 * @return the clinic
	 */
	public Clinic getClinic() {
		return clinic;
	}

	/**
	 * @param clinic the clinic to set
	 */
	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}
	
    @Override
    public String toString() {
        return "id = " + this.getId() + ", name = " + name + ", clinic = {" + this.clinic.toString() + "}"; 
    }
    
    @Override
    public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    
	    Doctor d = (Doctor) o;
	    if (this.getId() != null ? !this.getId().equals(d.getId()) : d.getId() != null) return false;
	    if (this.name != null ? !this.name.equals(d.getName()) : d.getName() != null) return false;
	    if (this.clinic != null ? !this.clinic.equals(d.getClinic()) : d.getClinic() != null) return false;
	    
        return true;
    }

    @Override
    public int hashCode() {
	    int result = this.getId() != null ? this.getId().hashCode() : 0;
	    result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
	    result = 31 * result + (this.clinic != null ? this.clinic.hashCode() : 0);
	    return result;
    }
	


}
