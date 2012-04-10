package org.motechproject.mrs.model;

import java.util.Date;
import java.util.Set;

/**
 * Class to maintain Patients visits as Encounters
 */
public class MRSEncounter {
    private String id;
    private MRSPerson provider;
    private MRSUser creator;
    private MRSFacility facility;
    private Date date;
    private Set<MRSObservation> observations;
    private MRSPatient patient;
    private String encounterType;

    public MRSEncounter() {
    }

    /**
     * Creates a MRS encounter object
     *
     * @param provider      Staff who provides information
     * @param creator       Staff who enters the details into the OpenMRS system
     * @param facility      Location of the encounter
     * @param date          Date of the encounter
     * @param patient       Patient involved in the encounter
     * @param observations  Observations collected during the encounter
     * @param encounterType Type of the encounter.
     */
    private MRSEncounter(MRSPerson provider, MRSUser creator, MRSFacility facility, Date date, MRSPatient patient, Set<MRSObservation> observations, String encounterType) {
        this.creator = creator;
        this.provider = provider;
        this.facility = facility;
        this.date = date;
        this.patient = patient;
        this.observations = observations;
        this.encounterType = encounterType;
    }

    /**
     * Creates a MRS encounter object
     *
     * @param id            Encounter Id
     * @param provider      Staff who provides information
     * @param staff         Staff who enters the details into the OpenMRS system
     * @param facility      Location of the encounter
     * @param date          Date of the encounter
     * @param patient       Patient involved in the encounter
     * @param observations  Observations collected during the encounter
     * @param encounterType Type of the encounter.
     */
    public MRSEncounter(String id, MRSPerson provider, MRSUser staff, MRSFacility facility, Date date, MRSPatient patient, Set<MRSObservation> observations, String encounterType) {
        this(provider, staff, facility, date, patient, observations, encounterType);
        this.id = id;
    }

    /**
     * Creates a MRS encounter object
     *
     * @param providerId    User ID of the Staff who provides information
     * @param creatorId     User ID of the Staff who enters the information into the system
     * @param facilityId    Location ID of the encounter
     * @param date          Date of the encounter
     * @param patientId     Patient ID of the encounter
     * @param observations  Observations collected in the encounter
     * @param encounterType Type of the encounter
     */
    public MRSEncounter(String providerId, String creatorId, String facilityId, Date date, String patientId, Set<MRSObservation> observations, String encounterType) {
        this(new MRSPerson().id(providerId), new MRSUser().id(creatorId), new MRSFacility(facilityId), date, new MRSPatient(patientId), observations, encounterType);
    }

    public MRSUser getCreator() {
        return creator;
    }

    public MRSPerson getProvider() {
        return provider;
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

    public String getId() {
        return id;
    }
    
    public MRSEncounter updateWithoutObs(MRSEncounter fromEncounter) {
        this.patient = fromEncounter.getPatient();
        this.creator = fromEncounter.getCreator();
        this.provider = fromEncounter.getProvider();
        this.facility = fromEncounter.getFacility();
        this.date = fromEncounter.getDate();
        this.encounterType = fromEncounter.getEncounterType();
        return this;
    }
}
