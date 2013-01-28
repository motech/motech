package org.motechproject.openmrs.services;

import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.model.OpenMRSEncounter;
import org.motechproject.mrs.model.OpenMRSEncounter.MRSEncounterBuilder;
import org.motechproject.mrs.model.OpenMRSObservation;
import org.motechproject.mrs.model.OpenMRSPatient;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.model.OpenMRSProvider;
import org.motechproject.mrs.model.OpenMRSUser;
import org.motechproject.mrs.services.EncounterAdapter;
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
public class OpenMRSEncounterAdapter implements EncounterAdapter {
    private EncounterService encounterService;
    private OpenMRSUserAdapter openMRSUserAdapter;
    private OpenMRSFacilityAdapter openMRSFacilityAdapter;
    private OpenMRSPatientAdapter openMRSPatientAdapter;
    private OpenMRSObservationAdapter openMRSObservationAdapter;
    private OpenMRSPersonAdapter openMRSPersonAdapter;

    @Autowired
    public OpenMRSEncounterAdapter(EncounterService encounterService, OpenMRSUserAdapter openMRSUserAdapter, OpenMRSFacilityAdapter openMRSFacilityAdapter, OpenMRSPatientAdapter openMRSPatientAdapter, OpenMRSObservationAdapter openMRSObservationAdapter, OpenMRSPersonAdapter openMRSPersonAdapter) {
        this.encounterService = encounterService;
        this.openMRSUserAdapter = openMRSUserAdapter;
        this.openMRSFacilityAdapter = openMRSFacilityAdapter;
        this.openMRSPatientAdapter = openMRSPatientAdapter;
        this.openMRSObservationAdapter = openMRSObservationAdapter;
        this.openMRSPersonAdapter = openMRSPersonAdapter;
    }

    /**
     * Saves the given MRS Encounter to the OpenMRS system
     * @param mrsEncounter The object to be saved
     * @return The saved instance of MRS Encounter
     */
    @Override
    @Transactional
    public OpenMRSEncounter createEncounter(org.motechproject.mrs.domain.Encounter mrsEncounter) {
        Encounter existingOpenMrsEncounter = findDuplicateOpenMrsEncounter(mrsEncounter);
        if (existingOpenMrsEncounter == null) {
            return openmrsToMrsEncounter(encounterService.saveEncounter(mrsToOpenMRSEncounter(mrsEncounter)));
        } else {
            encounterService.purgeEncounter(existingOpenMrsEncounter);
            return openmrsToMrsEncounter(encounterService.saveEncounter(mrsToOpenMRSEncounter(mrsEncounter)));
        }
    }

    Encounter findDuplicateOpenMrsEncounter(org.motechproject.mrs.domain.Encounter encounter) {
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
    public OpenMRSEncounter getLatestEncounterByPatientMotechId(String motechId, String encounterType) {
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

    OpenMRSEncounter openmrsToMrsEncounter(Encounter openMrsEncounter) {
        String id = Integer.toString(openMrsEncounter.getEncounterId());
        String encounterType = openMrsEncounter.getEncounterType().getName();
        Date date = openMrsEncounter.getEncounterDatetime();
        Facility facility = openMRSFacilityAdapter.convertLocationToFacility(openMrsEncounter.getLocation());
        OpenMRSPatient patient = openMRSPatientAdapter.getMrsPatient(openMrsEncounter.getPatient());
        Set<OpenMRSObservation> observations = openMRSObservationAdapter.convertOpenMRSToMRSObservations(openMrsEncounter.getObs());
        OpenMRSUser creator = new OpenMRSUser().systemId(openMrsEncounter.getCreator().getSystemId()).id(openMrsEncounter.getCreator().getId().toString());
        OpenMRSPerson person = new OpenMRSPerson().id(String.valueOf(openMrsEncounter.getProvider().getId()));
        OpenMRSProvider provider = new OpenMRSProvider(person);
        provider.setProviderId(String.valueOf(openMrsEncounter.getProvider().getId()));
        return new MRSEncounterBuilder().withId(id).withProvider(provider).withCreator(creator).withFacility(facility)
                .withDate(date).withPatient(patient).withObservations(observations).withEncounterType(encounterType).build();
    }

    Encounter mrsToOpenMRSEncounter(org.motechproject.mrs.domain.Encounter mrsEncounter) {
        org.openmrs.Encounter openMrsEncounter = new org.openmrs.Encounter();
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
    public OpenMRSEncounter getEncounterById(String encounterId) {
        Encounter encounter = encounterService.getEncounterByUuid(encounterId);
        if (encounter == null) {
            return null;
        } else {
            return openmrsToMrsEncounter(encounter);
        }
    }

    @Override
    public List<org.motechproject.mrs.domain.Encounter> getEncountersByEncounterType(String motechId, String encounterType) {
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
        
        ArrayList<org.motechproject.mrs.domain.Encounter> mrsEncounters = new ArrayList<org.motechproject.mrs.domain.Encounter>();
        
        for (Encounter encounter : encountersByType) {
            mrsEncounters.add(openmrsToMrsEncounter(encounter));
        }
        
        return mrsEncounters;
    }
}
