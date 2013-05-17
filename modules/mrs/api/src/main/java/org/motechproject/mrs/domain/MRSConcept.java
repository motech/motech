package org.motechproject.mrs.domain;

public interface MRSConcept {

    String getId();

    void setId(String id);

    String getUuid();

    void setUuid(String uuid);

    String getDisplay();

    void setDisplay(String display);

    MRSConceptName getName();

    void setName(MRSConceptName name);

    String getDataType();

    void setDataType(String dataType);

    String getConceptClass();

    void setConceptClass(String conceptClass);
}
