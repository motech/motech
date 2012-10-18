package org.motechproject.openmrs.ws.impl.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSEncounter.MRSEncounterBuilder;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.motechproject.openmrs.ws.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractEncounterIT {
    @Autowired
    private MRSUserAdapter userAdapter;

    @Autowired
    private MRSFacilityAdapter facilityAdapter;

    @Autowired
    private MRSEncounterAdapter encounterAdapter;

    @Autowired
    private MRSPatientAdapter patientAdapter;

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void shouldCreateEncounter() throws UserAlreadyExistsException, HttpException, ParseException {
        MRSUser user = userAdapter.getUserByUserName("chuck");
        String obsDate = "2012-09-05";
        MRSObservation ob = new MRSObservation<>(format.parse(obsDate), "Motech Concept", "Test");
        Set<MRSObservation> obs = new HashSet<>();
        obs.add(ob);

        MRSFacility facility = facilityAdapter.getFacilities("Clinic 1").get(0);
        MRSPatient patient = patientAdapter.getPatientByMotechId("700");
        MRSEncounter encounter = new MRSEncounterBuilder().withDate(format.parse(obsDate))
                .withEncounterType("ADULTINITIAL").withFacility(facility).withObservations(obs).withPatient(patient)
                .withProvider(user.getPerson()).build();

        MRSEncounter saved = encounterAdapter.createEncounter(encounter);
        assertNotNull(saved);
        assertNotNull(saved.getId());
    }

    @Test
    public void shouldGetLatestEncounter() {
        MRSEncounter encounter = encounterAdapter.getLatestEncounterByPatientMotechId("700", "ADULTINITIAL");

        assertNotNull(encounter);
        assertEquals("2012-09-07", format.format(encounter.getDate()));
    }

}
