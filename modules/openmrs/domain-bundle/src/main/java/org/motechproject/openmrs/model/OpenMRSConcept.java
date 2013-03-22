package org.motechproject.openmrs.model;

import org.motechproject.mrs.domain.MRSConcept;

/**
 * Maintains observation types
 */
public class OpenMRSConcept implements MRSConcept {
    private String name;

    /**
     * Creates a MRS concept with the given name
     * @param name Name of the concept
     */
    public OpenMRSConcept(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OpenMRSConcept)) {
            return false;
        }
        OpenMRSConcept that = (OpenMRSConcept) o;
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
