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

    @Override
    public MRSEncounter saveEncounter(MRSEncounter MRSEncounter) {
        Encounter savedEncounter = encounterService.saveEncounter(mrsToOpenMrsEncounter(MRSEncounter));
        return openMrsToMrsEncounter(savedEncounter);
    }

    public MRSEncounter openMrsToMrsEncounter(Encounter openMrsEncounter) {
        String id = Integer.toString(openMrsEncounter.getEncounterId());
        String encounterType = openMrsEncounter.getEncounterType().getName();
        Date date = openMrsEncounter.getEncounterDatetime();
        MRSUser staff = openMrsUserAdaptor.openMrsToMrsUser(openMrsEncounter.getCreator());
        MRSFacility facility = openMrsFacilityAdaptor.convertLocationToFacility(openMrsEncounter.getLocation());
        MRSPatient patient = openMrsPatientAdaptor.getMrsPatient(openMrsEncounter.getPatient());
        Set<Observation> observations = openMrsObservationAdaptor.getObservations(openMrsEncounter.getObs());
        return new MRSEncounter(id, staff, facility, date, patient, observations, encounterType);
    }

    public Encounter mrsToOpenMrsEncounter(MRSEncounter mrsEncounter) {
        org.openmrs.Encounter openMrsEncounter = new org.openmrs.Encounter();
        EncounterType openMrsEncounterType = encounterService.getEncounterType(mrsEncounter.getEncounterType());
        openMrsEncounter.setId(Integer.parseInt(mrsEncounter.getId()));
        openMrsEncounter.setEncounterType(openMrsEncounterType);
        openMrsEncounter.setEncounterDatetime(mrsEncounter.getDate());
        openMrsEncounter.setPatient(openMrsPatientAdaptor.getOpenMrsPatient(mrsEncounter.getPatient().getId()));
        openMrsEncounter.setLocation(openMrsFacilityAdaptor.getLocation(mrsEncounter.getFacility().getId()));
        openMrsEncounter.setCreator(openMrsUserAdaptor.getOpenMrsUserByUserName(mrsEncounter.getStaff().getId()));
        openMrsEncounter.setObs(openMrsObservationAdaptor.getOpenMrsObservations(mrsEncounter.getObservations()));
        return openMrsEncounter;
    }

}
