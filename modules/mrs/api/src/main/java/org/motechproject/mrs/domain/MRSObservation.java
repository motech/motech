package org.motechproject.mrs.domain;

import org.joda.time.DateTime;

import java.util.Set;

public interface MRSObservation<T> {

    String getPatientId();

    void setPatientId(String patientId);

    String getObservationId();

    void setObservationId(String observationId);

    DateTime getDate();

    void setDate(DateTime date);

    String getConceptName();

    void setConceptName(String conceptName);

    T getValue();

    void setValue(T value);

    Set<? extends MRSObservation> getDependantObservations();

    void setDependantObservations(Set<MRSObservation> dependantObservations);
}
