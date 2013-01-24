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
import org.motechproject.mrs.model.OpenMRSEncounter;
import org.motechproject.mrs.model.OpenMRSEncounter.MRSEncounterBuilder;
import org.motechproject.mrs.model.OpenMRSFacility;
import org.motechproject.mrs.model.OpenMRSObservation;
import org.motechproject.mrs.model.OpenMRSPatient;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.model.OpenMRSProvider;
import org.motechproject.mrs.model.OpenMRSUser;
import org.motechproject.mrs.services.EncounterAdapter;
import org.motechproject.mrs.services.FacilityAdapter;
import org.motechproject.mrs.services.PatientAdapter;
import org.motechproject.mrs.services.UserAdapter;
import org.motechproject.openmrs.ws.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractEncounterIT {
    @Autowired
    private UserAdapter userAdapter;

    @Autowired
    private FacilityAdapter facilityAdapter;

    @Autowired
    private EncounterAdapter encounterAdapter;

    @Autowired
    private PatientAdapter patientAdapter;

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void shouldCreateEncounter() throws UserAlreadyExistsException, HttpException, ParseException {
        OpenMRSUser user = (OpenMRSUser) userAdapter.getUserByUserName("chuck");
        String obsDate = "2012-09-05";
        OpenMRSObservation ob = new OpenMRSObservation<>(format.parse(obsDate), "Motech Concept", "Test");
        Set<OpenMRSObservation> obs = new HashSet<>();
        obs.add(ob);
        
        OpenMRSPerson person = user.getPerson();
        OpenMRSProvider provider = new OpenMRSProvider(person);
        provider.setProviderId(person.getPersonId());

        OpenMRSFacility facility = (OpenMRSFacility) facilityAdapter.getFacilities("Clinic 1").get(0);
        OpenMRSPatient patient = (OpenMRSPatient) patientAdapter.getPatientByMotechId("700");
        OpenMRSEncounter encounter = new MRSEncounterBuilder().withDate(format.parse(obsDate))
                .withEncounterType("ADULTINITIAL").withFacility(facility).withObservations(obs).withPatient(patient)
                .withProvider(provider).build();

        OpenMRSEncounter saved = (OpenMRSEncounter) encounterAdapter.createEncounter(encounter);
        assertNotNull(saved);
        assertNotNull(saved.getId());
    }

    @Test
    public void shouldGetLatestEncounter() {
        OpenMRSEncounter encounter = (OpenMRSEncounter) encounterAdapter.getLatestEncounterByPatientMotechId("700", "ADULTINITIAL");

        assertNotNull(encounter);
        assertEquals("2012-09-07", format.format(encounter.getDate()));
    }

}
