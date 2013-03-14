package org.motechproject.mrs.model;

import org.joda.time.DateTime;
import org.motechproject.mrs.domain.Encounter;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Observation;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Provider;
import org.motechproject.mrs.domain.User;

import java.util.Date;
import java.util.Set;

public class EncounterDto implements Encounter {

    private String id;
    private Provider provider;
    private User creator;
    private Facility facility;
    private Date date;
    private Set<Observation> observations;
    private Patient patient;
    private String encounterType;

    public EncounterDto() {
    }

    public EncounterDto(Provider provider, User creator, Facility facility, Date date, Set<Observation> observations, Patient patient, String encounterType) {
        this.provider = provider;
        this.creator = creator;
        this.facility = facility;
        this.date = date;
        this.observations = observations;
        this.patient = patient;
        this.encounterType = encounterType;
    }

    public String getEncounterId() {
        return id;
    }

    public void setEncounterId(String id) {
        this.id = id;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public DateTime getDate() {
        return (date != null) ? new DateTime(date) : null;
    }

    public void setDate(DateTime date) {
        this.date = date.toDate();
    }

    public Set<Observation> getObservations() {
        return observations;
    }

    public void setObservations(Set<? extends Observation> observations) {
        this.observations = (Set<Observation>) observations;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }
}
