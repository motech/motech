package org.motechproject.mrs.model;

/**
 * Maintains observation types
 */
public class MRSConcept {
    private String name;

    /**
     * Creates a MRS concept with the given name
     * @param name Name of the concept
     */
    public MRSConcept(String name) {
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
        if (!(o instanceof MRSConcept)) {
            return false;
        }
        MRSConcept that = (MRSConcept) o;
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
