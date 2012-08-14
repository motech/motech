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

    public static class MRSEncounterBuilder {
        private MRSPerson provider;
        private MRSUser creator;
        private MRSFacility facility;
        private Date date;
        private MRSPatient patient;
        private Set<MRSObservation> observations;
        private String encounterType;
        private String id;

        public MRSEncounterBuilder withProvider(MRSPerson provider) {
            this.provider = provider;
            return this;
        }

        public MRSEncounterBuilder withCreator(MRSUser creator) {
            this.creator = creator;
            return this;
        }

        public MRSEncounterBuilder withFacility(MRSFacility facility) {
            this.facility = facility;
            return this;
        }

        public MRSEncounterBuilder withDate(Date date) {
            this.date = date;
            return this;
        }

        public MRSEncounterBuilder withPatient(MRSPatient patient) {
            this.patient = patient;
            return this;
        }

        public MRSEncounterBuilder withObservations(Set<MRSObservation> observations) {
            this.observations = observations;
            return this;
        }

        public MRSEncounterBuilder withEncounterType(String encounterType) {
            this.encounterType = encounterType;
            return this;
        }

        public MRSEncounterBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public MRSEncounterBuilder withProviderId(String providerId) {
            this.provider = new MRSPerson().id(providerId);
            return this;
        }

        public MRSEncounterBuilder withCreatorId(String creatorId) {
            this.creator = new MRSUser().id(creatorId);
            return this;
        }

        public MRSEncounterBuilder withFacilityId(String facilityId) {
            this.facility = new MRSFacility(facilityId);
            return this;
        }

        public MRSEncounterBuilder withPatientId(String patientId) {
            this.patient = new MRSPatient(patientId);
            return this;
        }

        public MRSEncounter build() {
            MRSEncounter mrsEncounter = new MRSEncounter(provider, creator, facility, date, patient, observations, encounterType);
            mrsEncounter.id = this.id;
            return mrsEncounter;
        }
    }
}
