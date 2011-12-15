package org.motechproject.openmrs.services;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.*;
import org.motechproject.mrs.services.MRSEncounterAdaptor;
import org.motechproject.openmrs.OpenMRSIntegrationTestBase;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.motechproject.openmrs.services.OpenMRSUserAdaptor.USER_KEY;

public class OpenMRSEncounterAdaptorIT extends OpenMRSIntegrationTestBase {

    @Autowired
    MRSEncounterAdaptor mrsEncounterAdaptor;
    @Autowired
    EncounterService encounterService;
    @Autowired
    PatientService patientService;
    @Autowired
    UserService userService;

    MRSFacility facility;
    MRSPatient patientAlan;

    public void doOnceBefore() {
        facility = facilityAdaptor.saveFacility(new MRSFacility("name", "country", "region", "district", "province"));
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    @Ignore("To be verified for provider/creator for creating the encounter")
    public void testSaveEncounterWithObservations() throws UserAlreadyExistsException {
        patientAlan = createPatient(facility);

        MRSUser staff = new MRSUser().userName("SampleUserTest").systemId("1000544").firstName("SampleTest").securityRole("Provider");
        MRSEncounter mrsEncounter = null;
        try {
            staff = (MRSUser) userAdaptor.saveUser(staff).get(USER_KEY);

            Set<MRSObservation> observations = new HashSet<MRSObservation>();
            observations.add(new MRSObservation(new Date(), "Gravida", 100));
            observations.add(new MRSObservation(new Date(), "PARITY", "test"));
            observations.add(new MRSObservation(new Date(), "PARITY", new DateTime(2012, 11, 10, 1, 10).toDate()));
            observations.add(new MRSObservation(new Date(), "PARITY", false));
            mrsEncounter = new MRSEncounter(staff.getId(), facility.getId(), new Date(), patientAlan.getId(), observations, "PEDSRETURN");
            mrsEncounterAdaptor.createEncounter(mrsEncounter);
        }
        finally {
            final MRSUser copiedStaff = staff;
            authorizeAndRollback(new DirtyData() {
                public void rollback() {
                    userService.purgeUser(userService.getUser(Integer.valueOf(copiedStaff.getId())));
                    patientService.purgePatient(patientService.getPatient(Integer.valueOf(patientAlan.getId())));
//                    encounterService.purgeEncounter(encounterService.getEncounter(Integer.valueOf(mrsEncounter.getId())));
                }
            });
        }
    }

    private MRSPatient createPatient(MRSFacility facility) {
        final String first = "AlanTest";
        final String middle = "Wilkinson";
        final String last = "no";
        final String address1 = "a good street in ghana";
        final Date birthdate = new Date(1970, 3, 11);
        final String gender = "M";
        Boolean birthDateEstimated = true;

        final MRSPatient patient = new MRSPatient(randomId(), first, middle, last, "prefName", birthdate, birthDateEstimated, gender, address1, facility);
        return patientAdaptor.savePatient(patient);
    }

    private String randomId() {
        return "1000537";
    }
}
