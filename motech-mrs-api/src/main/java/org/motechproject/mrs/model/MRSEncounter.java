package org.motechproject.mrs.model;

import java.util.Date;
import java.util.Set;

public class MRSEncounter {
    public String getId() {
        return id;
    }

    private String id;
    private MRSUser staff;
    private Facility facility;
    private Date date;
    private Patient patient;
    private Set<Observation> observations;
    private String encounterType;

    public MRSEncounter() {
    }

    public MRSEncounter(MRSUser staff, Facility facility, Date date, Patient patient, Set<Observation> observations, String encounterType) {
        this.staff = staff;
        this.facility = facility;
        this.date = date;
        this.patient = patient;
        this.observations = observations;
        this.encounterType = encounterType;
    }

    public MRSEncounter(String id, MRSUser staff, Facility facility, Date date, Patient patient, Set<Observation> observations,String encounterType) {
        this(staff,facility,date,patient,observations,encounterType);
        this.id = id;
    }

    public MRSUser getStaff() {
        return staff;
    }

    public Facility getFacility() {
        return facility;
    }

    public Date getDate() {
        return date;
    }

    public Patient getPatient() {
        return patient;
    }

    public Set<Observation> getObservations() {
        return observations;
    }

    public String getEncounterType() {
        return encounterType;
    }
}