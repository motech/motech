package org.motechproject.openmrs.services;

import org.motechproject.mrs.model.*;
import org.motechproject.mrs.services.MRSEncounterAdaptor;
import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Set;

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
    public MRSEncounter createEncounter(MRSEncounter MRSEncounter) {
        Encounter savedEncounter = encounterService.saveEncounter(mrsToOpenMRSEncounter(MRSEncounter));
        return openmrsToMrsEncounter(savedEncounter);
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
        openMrsEncounter.setObs(openMrsObservationAdaptor.createOpenMRSObservationsForEncounter(mrsEncounter.getObservations(), openMrsEncounter, patient, location, creator));
        return openMrsEncounter;
    }

}
