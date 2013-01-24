package org.motechproject.mrs.model;

import java.util.Date;
import java.util.Set;

import org.joda.time.DateTime;
import org.motechproject.mrs.domain.Encounter;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Observation;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Provider;
import org.motechproject.mrs.domain.User;

/**
 * Class to maintain Patients visits as Encounters
 */
public class OpenMRSEncounter implements Encounter {

    private String id;
    private OpenMRSProvider provider;
    private OpenMRSUser creator;
    private OpenMRSFacility facility;
    private Date date;
    private Set<OpenMRSObservation> observations;
    private OpenMRSPatient patient;
    private String encounterType;

    public OpenMRSEncounter() {
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
    private OpenMRSEncounter(OpenMRSProvider provider, OpenMRSUser creator, OpenMRSFacility facility, Date date, OpenMRSPatient patient, Set<OpenMRSObservation> observations, String encounterType) {
        this.creator = creator;
        this.provider = provider;
        this.facility = facility;
        this.date = date;
        this.patient = patient;
        this.observations = observations;
        this.encounterType = encounterType;
    }

    public OpenMRSUser getCreator() {
        return creator;
    }

    public OpenMRSProvider getProvider() {
        return provider;
    }

    public OpenMRSFacility getFacility() {
        return facility;
    }

    public DateTime getDate() {
        return (date != null) ? new DateTime(date) : null;
    }

    public OpenMRSPatient getPatient() {
        return patient;
    }

    public Set<OpenMRSObservation> getObservations() {
        return observations;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public String getId() {
        return id;
    }

    public OpenMRSEncounter updateWithoutObs(OpenMRSEncounter fromEncounter) {
        this.patient = fromEncounter.getPatient();
        this.creator = fromEncounter.getCreator();
        this.provider = fromEncounter.getProvider();
        this.facility = fromEncounter.getFacility();
        if (fromEncounter.getDate() != null) {
            this.date = fromEncounter.getDate().toDate();
        }
        this.encounterType = fromEncounter.getEncounterType();
        return this;
    }

    public static class MRSEncounterBuilder {
        private OpenMRSProvider provider;
        private OpenMRSUser creator;
        private OpenMRSFacility facility;
        private Date date;
        private OpenMRSPatient patient;
        private Set<OpenMRSObservation> observations;
        private String encounterType;
        private String id;

        public MRSEncounterBuilder withProvider(OpenMRSProvider provider) {
            this.provider = provider;
            return this;
        }

        public MRSEncounterBuilder withCreator(OpenMRSUser creator) {
            this.creator = creator;
            return this;
        }

        public MRSEncounterBuilder withFacility(Facility facility2) {
            this.facility = (OpenMRSFacility) facility2;
            return this;
        }

        public MRSEncounterBuilder withDate(Date date) {
            this.date = date;
            return this;
        }

        public MRSEncounterBuilder withPatient(OpenMRSPatient patient) {
            this.patient = patient;
            return this;
        }

        public MRSEncounterBuilder withObservations(Set<? extends Observation> observations) {
            this.observations = (Set<OpenMRSObservation>) observations;
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
            this.provider = new OpenMRSProvider();
            this.provider.setProviderId(providerId);
            return this;
        }

        public MRSEncounterBuilder withCreatorId(String creatorId) {
            this.creator = new OpenMRSUser().id(creatorId);
            return this;
        }

        public MRSEncounterBuilder withFacilityId(String facilityId) {
            this.facility = new OpenMRSFacility(facilityId);
            return this;
        }

        public MRSEncounterBuilder withPatientId(String patientId) {
            this.patient = new OpenMRSPatient(patientId);
            return this;
        }

        public OpenMRSEncounter build() {
            OpenMRSEncounter mrsEncounter = new OpenMRSEncounter(provider, creator, facility, date, patient, observations, encounterType);
            mrsEncounter.id = this.id;
            return mrsEncounter;
        }
    }

    @Override
    public String getEncounterId() {
        return id;
    }

    @Override
    public void setEncounterId(String encounterId) {
        this.id = encounterId;
    }

    @Override
    public void setProvider(Provider provider) {
        this.provider = (OpenMRSProvider) provider;
    }

    @Override
    public void setCreator(User creator) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFacility(Facility facility) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDate(DateTime date) {
        this.date = date.toDate();
    }

    @Override
    public void setPatient(Patient patient) {
        this.patient = (OpenMRSPatient) patient;
    }

    @Override
    public void setEncounterType(String encounterType) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setObservations(Set<? extends Observation> observations) {
        this.observations = (Set<OpenMRSObservation>) observations;
    }
}
