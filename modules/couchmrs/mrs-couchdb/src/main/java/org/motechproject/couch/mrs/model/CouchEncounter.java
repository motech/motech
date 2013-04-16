package org.motechproject.couch.mrs.model;

import java.util.Set;
import java.util.UUID;
import org.joda.time.DateTime;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.domain.MRSUser;

public class CouchEncounter implements MRSEncounter {

    private String encounterId;
    private MRSProvider provider;
    private MRSUser creator;
    private MRSFacility facility;
    private DateTime date;
    private Set<? extends MRSObservation> observations;
    private MRSPatient patient;
    private String encounterType;

    public CouchEncounter(MRSProvider provider, MRSUser creator, MRSFacility facility, DateTime date, Set<? extends MRSObservation> observations, MRSPatient patient, String encounterType) {
        this.encounterId = UUID.randomUUID().toString();
        this.provider = provider;
        this.creator = creator;
        this.facility = facility;
        this.date = date;
        this.observations = observations;
        this.patient = patient;
        this.encounterType = encounterType;
    }

    public CouchEncounter(String encounterId, MRSProvider provider, MRSUser creator, MRSFacility facility, DateTime date, Set<? extends MRSObservation> observations, MRSPatient patient, String encounterType) {
        this(provider, creator, facility, date, observations, patient, encounterType);
        this.encounterId = encounterId;
    }

    @Override
    public String getEncounterId() {
        return encounterId;
    }

    @Override
    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    @Override
    public MRSProvider getProvider() {
        return provider;
    }

    @Override
    public void setProvider(MRSProvider provider) {
        this.provider = provider;
    }

    @Override
    public MRSUser getCreator() {
        return creator;
    }

    @Override
    public void setCreator(MRSUser creator) {
        this.creator = creator;
    }

    @Override
    public MRSFacility getFacility() {
        return facility;
    }

    @Override
    public void setFacility(MRSFacility facility) {
        this.facility = facility;
    }

    @Override
    public DateTime getDate() {
        return date;
    }

    @Override
    public void setDate(DateTime date) {
        this.date = date;
    }

    @Override
    public Set<? extends MRSObservation> getObservations() {
        return observations;
    }

    @Override
    public void setObservations(Set<? extends MRSObservation> observations) {
        this.observations = observations;
    }

    @Override
    public MRSPatient getPatient() {
        return patient;
    }

    @Override
    public void setPatient(MRSPatient patient) {
        this.patient = patient;
    }

    @Override
    public String getEncounterType() {
        return encounterType;
    }

    @Override
    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

}
