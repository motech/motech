package org.motechproject.openmrs.services;

import org.motechproject.mrs.model.*;
import org.motechproject.mrs.services.MRSEncounterAdaptor;
import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

public class OpenMRSEncounterAdaptor implements MRSEncounterAdaptor {
    @Autowired
    private EncounterService encounterService;
    @Autowired
    private OpenMRSUserAdaptor openMrsUserAdaptor;
    @Autowired
    private OpenMRSFacilityAdaptor openMrsFacilityAdaptor;
    @Autowired
    private OpenMRSPatientAdaptor openMrsPatientAdaptor;
    @Autowired
    private OpenMRSObservationAdaptor openMrsObservationAdaptor;
    @Autowired
    private OpenMRSPersonAdaptor openMRSPersonAdaptor;


    public OpenMRSEncounterAdaptor() {
    }

    @Override
    public MRSEncounter createEncounter(MRSEncounter mrsEncounter) {
        Encounter savedEncounter = encounterService.saveEncounter(mrsToOpenMRSEncounter(mrsEncounter));
        return openmrsToMrsEncounter(savedEncounter);
    }

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
        MRSUser creator = openMrsUserAdaptor.openMrsToMrsUser(openMrsEncounter.getCreator());
        MRSPerson provider = openMRSPersonAdaptor.openMRSToMRSPerson(openMrsEncounter.getProvider());
        MRSFacility facility = openMrsFacilityAdaptor.convertLocationToFacility(openMrsEncounter.getLocation());
        MRSPatient patient = openMrsPatientAdaptor.getMrsPatient(openMrsEncounter.getPatient());
        Set<MRSObservation> observations = openMrsObservationAdaptor.convertOpenMRSToMRSObservations(openMrsEncounter.getObs());
        return new MRSEncounter(id, provider, creator, facility, date, patient, observations, encounterType);
    }

    Encounter mrsToOpenMRSEncounter(MRSEncounter mrsEncounter) {
        org.openmrs.Encounter openMrsEncounter = new org.openmrs.Encounter();
        EncounterType openMrsEncounterType = encounterService.getEncounterType(mrsEncounter.getEncounterType());
        Patient patient = openMrsPatientAdaptor.getOpenMrsPatient(mrsEncounter.getPatient().getId());
        User creator = openMrsUserAdaptor.getOpenMrsUserById(mrsEncounter.getCreator().getId());
        Location location = openMrsFacilityAdaptor.getLocation(mrsEncounter.getFacility().getId());
        Person provider = openMRSPersonAdaptor.getPersonById(mrsEncounter.getProvider().getId());
        openMrsEncounter.setEncounterType(openMrsEncounterType);
        openMrsEncounter.setEncounterDatetime(mrsEncounter.getDate());
        openMrsEncounter.setPatient(patient);
        openMrsEncounter.setLocation(location);
        openMrsEncounter.setCreator(creator);
        openMrsEncounter.setProvider(provider);
        if (mrsEncounter.getObservations() != null) {
            openMrsEncounter.setObs(openMrsObservationAdaptor.createOpenMRSObservationsForEncounter(mrsEncounter.getObservations(), openMrsEncounter, patient, location, creator));
        }
        return openMrsEncounter;
    }

}
