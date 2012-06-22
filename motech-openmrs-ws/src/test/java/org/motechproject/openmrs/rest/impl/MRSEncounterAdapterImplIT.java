package org.motechproject.openmrs.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationOpenMrsWS.xml" })
public class MRSEncounterAdapterImplIT {

    private static final String ENCOUNTER_TYPE = "ADULTINITIAL";

    private MRSFacility facility;
    private MRSPatient patient;
    private String tempConceptUuid;
    private MRSUser creator;

    @Autowired
    MRSEncounterAdapter encounterAdapter;

    @Autowired
    MRSConceptAdapterImpl conceptAdapter;

    @Autowired
    RestClient restfulClient;

    @Autowired
    AdapterHelper adapterHelper;

    @Value("${openmrs.url}")
    String openmrsUrl;

    @Before
    public void before() {
        facility = null;
        patient = null;
        tempConceptUuid = null;
        creator = null;
        conceptAdapter.clearCachedConcepts();
    }

    @Test
    public void shouldCreateEncounter() throws HttpException, URISyntaxException, UserAlreadyExistsException {
        MRSEncounter persistedEncounter = null;
        try {
            createRequiredEntities();

            persistedEncounter = createEncounterWithSingleObservation();
            assertNotNull(persistedEncounter.getId());
        } finally {
            adapterHelper.deleteEncounter(persistedEncounter);
            deleteCreatedEntities();
        }
    }

    private MRSEncounter createEncounterWithSingleObservation() {
        MRSEncounter persistedEncounter;
        Set<MRSObservation> obs = new HashSet<MRSObservation>();
        MRSObservation ob = new MRSObservation(Calendar.getInstance().getTime(), AdapterHelper.TEST_CONCEPT_NAME,
                "Test Value");
        obs.add(ob);

        persistedEncounter = createEncounter(obs, TestUtils.CURRENT_DATE);
        return persistedEncounter;
    }

    @Test
    public void shouldFindLatestEncounter() throws HttpException, URISyntaxException, UserAlreadyExistsException {
        List<MRSEncounter> persistedEncounters = null;
        try {
            createRequiredEntities();
            persistedEncounters = createMultipleEncounterWithObservation();

            MRSEncounter encounter = encounterAdapter.getLatestEncounterByPatientMotechId(TestUtils.MOTECH_ID_1, null);
            assertEquals(TestUtils.CURRENT_DATE, encounter.getDate());
        } finally {
            adapterHelper.deleteEncounter(persistedEncounters.get(0));
            adapterHelper.deleteEncounter(persistedEncounters.get(1));
            deleteCreatedEntities();
        }
    }

    private List<MRSEncounter> createMultipleEncounterWithObservation() {
        List<MRSEncounter> encounters = new ArrayList<MRSEncounter>();
        MRSEncounter persistedEncounter;
        Set<MRSObservation> obs = new HashSet<MRSObservation>();
        MRSObservation ob = new MRSObservation(Calendar.getInstance().getTime(), AdapterHelper.TEST_CONCEPT_NAME,
                "Test Value");
        obs.add(ob);

        persistedEncounter = createEncounter(obs, TestUtils.CURRENT_DATE);
        encounters.add(persistedEncounter);

        Calendar pastDate = Calendar.getInstance();
        pastDate.add(Calendar.DATE, -100);
        obs = new HashSet<MRSObservation>();
        ob = new MRSObservation(Calendar.getInstance().getTime(), AdapterHelper.TEST_CONCEPT_NAME, "Test Value");
        obs.add(ob);

        persistedEncounter = createEncounter(obs, pastDate.getTime());
        encounters.add(persistedEncounter);

        return encounters;
    }

    private MRSEncounter createEncounter(Set<MRSObservation> obs, Date encounterDate) {
        MRSEncounter encounter = new MRSEncounter(creator.getPerson().getId(), creator.getId(), facility.getId(),
                encounterDate, patient.getId(), obs, ENCOUNTER_TYPE);

        return encounterAdapter.createEncounter(encounter);
    }

    private void createRequiredEntities() throws HttpException, URISyntaxException, UserAlreadyExistsException {
        facility = adapterHelper.createTemporaryLocation();
        patient = adapterHelper.createTemporaryPatient(TestUtils.MOTECH_ID_1, TestUtils.makePerson(), facility);
        tempConceptUuid = adapterHelper.createTemporaryConcept(AdapterHelper.TEST_CONCEPT_NAME);
        creator = adapterHelper.createTemporaryProvider();
    }

    private void deleteCreatedEntities() throws HttpException, URISyntaxException {
        adapterHelper.deleteConcept(tempConceptUuid);
        adapterHelper.deletePatient(patient);
        adapterHelper.deleteUser(creator);
        adapterHelper.deleteFacility(facility);
    }
}
