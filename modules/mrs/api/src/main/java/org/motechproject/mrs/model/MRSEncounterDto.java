package org.motechproject.mrs.model;

import org.joda.time.DateTime;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.domain.MRSUser;

import java.util.Date;
import java.util.Set;

public class MRSEncounterDto implements MRSEncounter {

    private String id;
    private MRSProvider provider;
    private MRSUser creator;
    private MRSFacility facility;
    private Date date;
    private Set<MRSObservation> observations;
    private MRSPatient patient;
    private String encounterType;

    public MRSEncounterDto() {
    }

    public MRSEncounterDto(MRSProvider provider, MRSUser creator, MRSFacility facility, Date date, Set<MRSObservation> observations, MRSPatient patient, String encounterType) {
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

    public MRSProvider getProvider() {
        return provider;
    }

    public void setProvider(MRSProvider provider) {
        this.provider = provider;
    }

    public MRSUser getCreator() {
        return creator;
    }

    public void setCreator(MRSUser creator) {
        this.creator = creator;
    }

    public MRSFacility getFacility() {
        return facility;
    }

    public void setFacility(MRSFacility facility) {
        this.facility = facility;
    }

    public DateTime getDate() {
        return (date != null) ? new DateTime(date) : null;
    }

    public void setDate(DateTime date) {
        this.date = date.toDate();
    }

    public Set<MRSObservation> getObservations() {
        return observations;
    }

    public void setObservations(Set<? extends MRSObservation> observations) {
        this.observations = (Set<MRSObservation>) observations;
    }

    public MRSPatient getPatient() {
        return patient;
    }

    public void setPatient(MRSPatient patient) {
        this.patient = patient;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }
}
