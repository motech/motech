package org.motechproject.couch.mrs.model;

import java.util.Set;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'Observation'")
public class CouchObservationImpl<T> extends MotechBaseDataObject {

    private static final long serialVersionUID = 1L;

    @JsonProperty
    private String observationId;
    @JsonProperty
    private DateTime date;
    @JsonProperty
    private String conceptName;
    @JsonProperty    
    private String patientId;
    @JsonProperty    
    private T value;
    @JsonProperty    
    private Set<String> dependantObservationIds;

    private final String type = "Observation";

    public CouchObservationImpl() {
        super();
        this.setType(type);
    }

    public CouchObservationImpl(String observationId, DateTime date, String conceptName, String patientId, T value, Set<String> dependantObservationIds) {
        this();
        this.observationId = observationId;
        this.date = date;
        this.conceptName = conceptName;
        this.patientId = patientId;
        this.value = value;
        this.dependantObservationIds = dependantObservationIds;
    }

    public String getObservationId() {
        return observationId;
    }

    public void setObservationId(String observationId) {
        this.observationId = observationId;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Set<String> getDependantObservationIds() {
        return dependantObservationIds;
    }

    public void setDependantObservationIds(Set<String> dependantObservationIds) {
        this.dependantObservationIds = dependantObservationIds;
    }

    public String getType() {
        return type;
    }
}
