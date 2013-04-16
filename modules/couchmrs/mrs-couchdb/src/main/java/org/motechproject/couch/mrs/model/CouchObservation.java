package org.motechproject.couch.mrs.model;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;
import org.motechproject.mrs.domain.MRSObservation;
import ch.lambdaj.Lambda;

public class CouchObservation<T> implements MRSObservation<T> {

    private String patientId;
    private String observationId;
    private DateTime date;
    private String conceptName;
    private T value;
    private Set<MRSObservation> dependantObservations;

    public CouchObservation(DateTime date, String conceptName, T value) {
        this.observationId = UUID.randomUUID().toString();
        this.date = date;
        this.conceptName = conceptName;
        this.value = value;
    }

    public CouchObservation(DateTime date, String conceptName, T value, String patientId) {
        this(date, conceptName, value);
        this.patientId = patientId;
    }

    public CouchObservation(String observationId, DateTime date, String conceptName, T value) {
        this(date, conceptName, value);
        this.observationId = observationId;
    }

    public CouchObservation(String observationId, DateTime date, String conceptName, T value, String patientId) {
        this(observationId, date, conceptName, value);
        this.patientId = patientId;
    }

    
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
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

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public Set<MRSObservation> getDependantObservations() {
        return dependantObservations;
    }

    @Override
    public void setDependantObservations(Set<MRSObservation> dependantObservations) {
        this.dependantObservations = dependantObservations;
    }

    public void addDependantObservation(MRSObservation mrsObservation) {

        if (this.dependantObservations == null) {
            dependantObservations = new HashSet<>();
        }

        List<? extends MRSObservation> existingObservationList = Lambda.filter(having(on(CouchObservation.class).getConceptName(), is(equalTo(mrsObservation.getConceptName()))), dependantObservations);
        if (!existingObservationList.isEmpty()) {
            dependantObservations.remove(existingObservationList.get(0));
        }

        dependantObservations.add(mrsObservation);
    }

    @Override
    public int hashCode() {
        int result = observationId != null ? observationId.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (conceptName != null ? conceptName.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (dependantObservations != null ? dependantObservations.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MRSObservation{" +
                "id='" + observationId + '\'' +
                ", date=" + date +
                ", conceptName='" + conceptName + '\'' +
                ", value=" + value +
                '}';
    }
}
