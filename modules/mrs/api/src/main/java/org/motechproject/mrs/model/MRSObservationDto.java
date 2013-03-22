package org.motechproject.mrs.model;

import org.joda.time.DateTime;
import org.motechproject.mrs.domain.MRSObservation;

import java.util.Date;
import java.util.Set;

public class MRSObservationDto implements MRSObservation<Object> {
    private String id;
    private Date date;
    private String conceptName;
    private String patientId;
    private Object value;
    private Set<MRSObservation> dependantObservations;

    public MRSObservationDto() {
    }

    public MRSObservationDto(String id, Date date, String conceptName, String patientId, Object value, Set<MRSObservation> dependantObservations) {
        this.id = id;
        this.date = date;
        this.conceptName = conceptName;
        this.patientId = patientId;
        this.value = value;
        this.dependantObservations = dependantObservations;
    }

    public MRSObservationDto(Date date, String conceptName, String patientId, Object value) {
        this.date = date;
        this.conceptName = conceptName;
        this.patientId = patientId;
        this.value = value;
    }

    public MRSObservationDto(Date date, String conceptName, Object value) {
        this.date = date;
        this.conceptName = conceptName;
        this.value = value;
    }

    public String getObservationId() {
        return id;
    }

    public void setObservationId(String id) {
        this.id = id;
    }

    public DateTime getDate() {
        return (date != null) ? new DateTime(date) : null;
    }

    public void setDate(DateTime date) {
        this.date = date.toDate();
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

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Set<MRSObservation> getDependantObservations() {
        return dependantObservations;
    }

    public void setDependantObservations(Set<MRSObservation> dependantObservations) {
        this.dependantObservations = (Set<MRSObservation>) dependantObservations;
    }

}

