package org.motechproject.mrs.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.motechproject.mrs.domain.MRSConcept;
import org.motechproject.mrs.domain.MRSConceptName;

public class MRSConceptDto implements MRSConcept{
    @JsonDeserialize(as = MRSConceptNameDto.class)
    private MRSConceptName name;

    private String id;
    private String uuid;
    private String dataType;
    private String conceptClass;
    private String display;

    public MRSConceptDto() {
    }

    public MRSConceptDto(MRSConceptName name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
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

    public MRSConceptName getName() {

        return name;
    }

    public void setName(MRSConceptName name) {
        this.name = name;
    }
}
