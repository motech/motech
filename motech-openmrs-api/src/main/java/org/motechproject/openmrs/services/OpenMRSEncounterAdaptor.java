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
        Set<MRSObservation> observations = openMrsObservationAdaptor.convertOpenMRSToMRSObservations(openMrsEncounter.getObs());
        return new MRSEncounter(id, staff, facility, date, patient, observations, encounterType);
    }

    public Encounter mrsToOpenMrsEncounter(MRSEncounter mrsEncounter) {
        org.openmrs.Encounter openMrsEncounter = new org.openmrs.Encounter();
        EncounterType openMrsEncounterType = encounterService.getEncounterType(mrsEncounter.getEncounterType());
        Patient patient = openMrsPatientAdaptor.getOpenMrsPatient(mrsEncounter.getPatient().getId());
        User staff = openMrsUserAdaptor.getOpenMrsUserByUserName(mrsEncounter.getStaff().getId());
        Location facility = openMrsFacilityAdaptor.getLocation(mrsEncounter.getFacility().getId());
        openMrsEncounter.setId(Integer.parseInt(mrsEncounter.getId()));
        openMrsEncounter.setEncounterType(openMrsEncounterType);
        openMrsEncounter.setEncounterDatetime(mrsEncounter.getDate());
        openMrsEncounter.setPatient(patient);
        openMrsEncounter.setLocation(facility);
        openMrsEncounter.setCreator(staff);
        openMrsEncounter.setObs(openMrsObservationAdaptor.createOpenMRSObservationForEncounters(mrsEncounter.getObservations(), openMrsEncounter, patient, facility, staff));
        return openMrsEncounter;
    }

}
