package org.motechproject.couch.mrs.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.mrs.domain.MRSConcept;
import org.motechproject.mrs.domain.MRSConceptName;

@TypeDiscriminator("doc.type === 'Concept'")
public class CouchConcept extends MotechBaseDataObject implements MRSConcept {

    private static final long serialVersionUID = 1L;

    @JsonDeserialize(as = CouchConceptName.class)
    private CouchConceptName name;

    private String uuid;
    private String dataType;
    private String conceptClass;
    private String display;

    private final String type = "Concept";

    public CouchConcept() {
        this.setType(type);
    }

    public CouchConcept(String name) {
        this();
        this.name = new CouchConceptName(name);
    }

    public CouchConceptName getName() {
        return name;
    }

    @Override
    public void setName(MRSConceptName name) {
        this.name = (name instanceof CouchConceptName) ? (CouchConceptName) name :
                new CouchConceptName(name.getName(), name.getLocale(), name.getConceptNameType());
    }

    public void setName(CouchConceptName name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
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
}
