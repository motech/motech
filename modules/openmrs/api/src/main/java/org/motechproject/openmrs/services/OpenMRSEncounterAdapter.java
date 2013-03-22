package org.motechproject.openmrs.services;


import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.domain.MRSUser;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.openmrs.model.OpenMRSEncounter;
import org.motechproject.openmrs.model.OpenMRSPerson;
import org.motechproject.openmrs.model.OpenMRSProvider;
import org.motechproject.openmrs.model.OpenMRSUser;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.EncounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;
import static java.util.Arrays.asList;

/**
 * Manages OpenMRS Encounters
 */
@Service
public class OpenMRSEncounterAdapter implements MRSEncounterAdapter {
    private EncounterService encounterService;
    private OpenMRSUserAdapter openMRSUserAdapter;
    private OpenMRSFacilityAdapter openMRSFacilityAdapter;
    private OpenMRSPatientAdapter openMRSPatientAdapter;
    private OpenMRSObservationAdapter openMRSObservationAdapter;
    private OpenMRSPersonAdapter openMRSPersonAdapter;
    private EventRelay eventRelay;

    @Autowired
    public OpenMRSEncounterAdapter(EncounterService encounterService, OpenMRSUserAdapter openMRSUserAdapter, OpenMRSFacilityAdapter openMRSFacilityAdapter, OpenMRSPatientAdapter openMRSPatientAdapter, OpenMRSObservationAdapter openMRSObservationAdapter, OpenMRSPersonAdapter openMRSPersonAdapter, EventRelay eventRelay) {
        this.encounterService = encounterService;
        this.openMRSUserAdapter = openMRSUserAdapter;
        this.openMRSFacilityAdapter = openMRSFacilityAdapter;
        this.openMRSPatientAdapter = openMRSPatientAdapter;
        this.openMRSObservationAdapter = openMRSObservationAdapter;
        this.openMRSPersonAdapter = openMRSPersonAdapter;
        this.eventRelay = eventRelay;
    }

    /**
     * Saves the given MRS Encounter to the OpenMRS system
     * @param mrsEncounter The object to be saved
     * @return The saved instance of MRS Encounter
     */
    @Override
    @Transactional
    public MRSEncounter createEncounter(MRSEncounter mrsEncounter) {
        Encounter existingOpenMrsEncounter = findDuplicateOpenMrsEncounter(mrsEncounter);
        if (existingOpenMrsEncounter == null) {
            MRSEncounter encounter = openmrsToMrsEncounter(encounterService.saveEncounter(mrsToOpenMRSEncounter(mrsEncounter)));
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_ENCOUNTER_SUBJECT, EventHelper.encounterParameters(encounter)));
            return encounter;
        } else {
            encounterService.purgeEncounter(existingOpenMrsEncounter);
            MRSEncounter encounter = openmrsToMrsEncounter(encounterService.saveEncounter(mrsToOpenMRSEncounter(mrsEncounter)));
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.UPDATED_ENCOUNTER_SUBJECT, EventHelper.encounterParameters(encounter)));
            return encounter;
        }
    }

    Encounter findDuplicateOpenMrsEncounter(MRSEncounter encounter) {
        Patient patient = openMRSPatientAdapter.getOpenMrsPatient(encounter.getPatient().getPatientId());
        EncounterType encounterType = encounterService.getEncounterType(encounter.getEncounterType());
        Date encounterTime = encounter.getDate().toDate();
        List<Encounter> encounters = encounterService.getEncounters(patient, null, encounterTime, encounterTime, null, asList(encounterType), null, false);
        return encounters.size() > 0 ? encounters.get(0) : null;
    }

    /**
     * Fetches the latest encounter of a patient identified by MOTECH ID and the encounter type.
     * @param motechId Identifier of the patient
     * @param encounterType Type of the encounter. (e.g. ANCVISIT)
     * @return The latest MRSEncounter if found, else null.
     */
    @Override
    public MRSEncounter getLatestEncounterByPatientMotechId(String motechId, String encounterType) {
        final List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(motechId);
        final ArrayList<Encounter> encountersByType = new ArrayList<Encounter>();
        for (Encounter encounter : encounters) {
            if (encounterType.equals(encounter.getEncounterType().getName())) {
                encountersByType.add(encounter);
            }
        }
        if (encountersByType.isEmpty()) {
            return null;
        }
        if (encountersByType.size() == 1) {
            return openmrsToMrsEncounter(encountersByType.get(0));
        }
        final List<Object> sortedEncounters = sort(encountersByType,
                on(Encounter.class).getEncounterDatetime());
        return openmrsToMrsEncounter((Encounter) sortedEncounters.get(sortedEncounters.size() - 1));
    }

    MRSEncounter openmrsToMrsEncounter(Encounter openMrsEncounter) {
        String id = Integer.toString(openMrsEncounter.getEncounterId());
        String encounterType = openMrsEncounter.getEncounterType().getName();
        Date date = openMrsEncounter.getEncounterDatetime();
        MRSFacility facility = openMRSFacilityAdapter.convertLocationToFacility(openMrsEncounter.getLocation());
        MRSPatient patient = openMRSPatientAdapter.getMrsPatient(openMrsEncounter.getPatient());
        Set<? extends MRSObservation> observations = openMRSObservationAdapter.convertOpenMRSToMRSObservations(openMrsEncounter.getObs());
        MRSUser creator = new OpenMRSUser().systemId(openMrsEncounter.getCreator().getSystemId()).id(openMrsEncounter.getCreator().getId().toString());
        MRSPerson person = new OpenMRSPerson().id(String.valueOf(openMrsEncounter.getProvider().getId()));
        MRSProvider provider = new OpenMRSProvider(person);
        provider.setProviderId(String.valueOf(openMrsEncounter.getProvider().getId()));
        return new OpenMRSEncounter.MRSEncounterBuilder().withId(id).withProvider(provider).withCreator(creator).withFacility(facility)
                .withDate(date).withPatient(patient).withObservations(observations).withEncounterType(encounterType).build();
    }

    Encounter mrsToOpenMRSEncounter(MRSEncounter mrsEncounter) {
        Encounter openMrsEncounter = new Encounter();
        EncounterType openMrsEncounterType = encounterService.getEncounterType(mrsEncounter.getEncounterType());
        Patient patient = openMRSPatientAdapter.getOpenMrsPatient(mrsEncounter.getPatient().getPatientId());
        User creator = openMRSUserAdapter.getOpenMrsUserById(mrsEncounter.getCreator().getUserId());
        Location location = openMRSFacilityAdapter.getLocation(mrsEncounter.getFacility().getFacilityId());
        Person provider = openMRSPersonAdapter.getPersonById(mrsEncounter.getProvider().getProviderId());
        openMrsEncounter.setEncounterType(openMrsEncounterType);
        if (mrsEncounter.getDate() != null) {
            openMrsEncounter.setEncounterDatetime(mrsEncounter.getDate().toDate());            
        }
        openMrsEncounter.setPatient(patient);
        openMrsEncounter.setLocation(location);
        openMrsEncounter.setCreator(creator);
        openMrsEncounter.setProvider(provider);
        if (mrsEncounter.getObservations() != null) {
            openMrsEncounter.setObs(openMRSObservationAdapter.createOpenMRSObservationsForEncounter(mrsEncounter.getObservations(), openMrsEncounter, patient, location, creator));
        }
        return openMrsEncounter;
    }

    @Override
    public MRSEncounter getEncounterById(String encounterId) {
        Encounter encounter = encounterService.getEncounterByUuid(encounterId);
        if (encounter == null) {
            return null;
        } else {
            return openmrsToMrsEncounter(encounter);
        }
    }

    @Override
    public List<MRSEncounter> getEncountersByEncounterType(String motechId, String encounterType) {
        final List<Encounter> encounters = encounterService.getEncountersByPatientIdentifier(motechId);
        final ArrayList<Encounter> encountersByType = new ArrayList<>();
        for (Encounter encounter : encounters) {
            if (encounterType.equals(encounter.getEncounterType().getName())) {
                encountersByType.add(encounter);
            }
        }
        if (encountersByType.isEmpty()) {
            return null;
        }
        
        ArrayList<MRSEncounter> mrsEncounters = new ArrayList<>();
        
        for (Encounter encounter : encountersByType) {
            mrsEncounters.add(openmrsToMrsEncounter(encounter));
        }
        
        return mrsEncounters;
    }
}
