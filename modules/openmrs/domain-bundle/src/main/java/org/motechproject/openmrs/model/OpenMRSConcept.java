package org.motechproject.openmrs.model;

import org.motechproject.mrs.domain.MRSConcept;
import org.motechproject.mrs.domain.MRSConceptName;

/**
 * Maintains observation types
 */
public class OpenMRSConcept implements MRSConcept {
    private MRSConceptName name;
    private String id;
    private String uuid;
    private String dataType;
    private String conceptClass;
    private String display;

    public OpenMRSConcept(MRSConceptName name){
        this.name = name;
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
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public MRSConceptName getName() {
        return name;
    }

    public void setName(MRSConceptName name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getConceptClass() {
        return conceptClass;
    }

    public void setConceptClass(String conceptClass) {
        this.conceptClass = conceptClass;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
}
