package org.motechproject.openmrs.model;

import org.joda.time.DateTime;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.domain.MRSUser;

import java.util.Date;
import java.util.Set;

/**
 * Class to maintain Patients visits as Encounters
 */
public class OpenMRSEncounter implements MRSEncounter {

    private String id;
    private MRSProvider provider;
    private MRSUser creator;
    private MRSFacility facility;
    private Date date;
    private Set<? extends MRSObservation> observations;
    private MRSPatient patient;
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
    private OpenMRSEncounter(MRSProvider provider, MRSUser creator, MRSFacility facility, Date date, MRSPatient patient, Set<? extends MRSObservation> observations, String encounterType) {
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

    public MRSProvider getProvider() {
        return provider;
    }

    public MRSFacility getFacility() {
        return facility;
    }

    public DateTime getDate() {
        return (date != null) ? new DateTime(date) : null;
    }

    public MRSPatient getPatient() {
        return patient;
    }

    public Set<? extends MRSObservation> getObservations() {
        return observations;
    }

    public String getEncounterType() {
        return encounterType;
    }

    @Deprecated
    public String getId() {
        return id;
    }

    public MRSEncounter updateWithoutObs(MRSEncounter fromEncounter) {
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
        private MRSProvider provider;
        private MRSUser creator;
        private MRSFacility facility;
        private Date date;
        private MRSPatient patient;
        private Set<? extends MRSObservation> observations;
        private String encounterType;
        private String id;

        public MRSEncounterBuilder withProvider(MRSProvider provider) {
            this.provider = provider;
            return this;
        }

        public MRSEncounterBuilder withCreator(MRSUser creator) {
            this.creator = creator;
            return this;
        }

        public MRSEncounterBuilder withFacility(MRSFacility facility2) {
            this.facility = facility2;
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

        public MRSEncounterBuilder withObservations(Set<? extends MRSObservation> observations) {
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
    public void setProvider(MRSProvider provider) {
        this.provider = provider;
    }

    @Override
    public void setCreator(MRSUser creator) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFacility(MRSFacility facility) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDate(DateTime date) {
        this.date = date.toDate();
    }

    @Override
    public void setPatient(MRSPatient patient) {
        this.patient = patient;
    }

    @Override
    public void setEncounterType(String encounterType) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setObservations(Set<? extends MRSObservation> observations) {
        this.observations = observations;
    }
}
