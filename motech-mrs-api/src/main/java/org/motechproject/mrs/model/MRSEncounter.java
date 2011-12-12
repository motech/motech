package org.motechproject.mrs.model;

import java.util.Date;
import java.util.Set;

public class MRSEncounter {
    public String getId() {
        return id;
    }

    private String id;
    private MRSUser staff;
    private MRSFacility facility;
    private Date date;
    private Set<MRSObservation> observations;
    private MRSPatient patient;
    private String encounterType;

    public MRSEncounter() {
    }

    public MRSEncounter(MRSUser staff, MRSFacility facility, Date date, MRSPatient patient, Set<MRSObservation> observations, String encounterType) {
        this.staff = staff;
        this.facility = facility;
        this.date = date;
        this.patient = patient;
        this.observations = observations;
        this.encounterType = encounterType;
    }

    public MRSEncounter(String id, MRSUser staff, MRSFacility facility, Date date, MRSPatient patient, Set<MRSObservation> observations,String encounterType) {
        this(staff,facility,date,patient,observations,encounterType);
        this.id = id;
    }

    public MRSUser getStaff() {
        return staff;
    }

    public MRSFacility getFacility() {
        return facility;
    }

    public Date getDate() {
        return date;
    }

    public MRSPatient getPatient() {
        return patient;
    }

    public Set<MRSObservation> getObservations() {
        return observations;
    }

    public String getEncounterType() {
        return encounterType;
    }
}