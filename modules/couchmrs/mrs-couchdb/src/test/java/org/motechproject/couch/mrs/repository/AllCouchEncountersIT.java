package org.motechproject.couch.mrs.repository;

import static org.junit.Assert.assertEquals;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchEncounterImpl;
import org.motechproject.couch.mrs.repository.impl.AllCouchEncountersImpl;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class AllCouchEncountersIT extends SpringIntegrationTest {

    @Autowired
    private AllCouchEncounters allCouchEncounters;

    @Autowired
    @Qualifier("couchEncounterDatabaseConnector")
    private CouchDbConnector connector;

    @Test
    public void shouldSaveEncounterAndRetrieveById() {
        CouchEncounterImpl encounter = buildEncounter("patientId", "encounterType");

        allCouchEncounters.createOrUpdateEncounter(encounter);

        CouchEncounterImpl retrievedEncounter = allCouchEncounters.findEncounterById(encounter.getEncounterId());

        assertEquals("patientId", retrievedEncounter.getPatientId());
        assertEquals("encounterType", retrievedEncounter.getEncounterType());
        assertEquals(encounter.getEncounterId(), retrievedEncounter.getEncounterId());
        assertEquals(encounter.getCreatorId(), retrievedEncounter.getCreatorId());
        assertEquals(encounter.getFacilityId(), retrievedEncounter.getFacilityId());
        assertEquals(encounter.getProviderId(), retrievedEncounter.getProviderId());
        assertEquals(encounter.getObservationIds(), retrievedEncounter.getObservationIds());
    }

    @Test
    public void shouldFindByMotechIdAndEncounterType() {
        CouchEncounterImpl encounter = buildEncounter("patientId1", "encounterType1");
        CouchEncounterImpl encounter2 = buildEncounter("patientId1", "encounterType1");
        CouchEncounterImpl encounter3 = buildEncounter("patientId1", "encounterType1");
        CouchEncounterImpl encounter4 = buildEncounter("patientId1", "encounterType2");
        CouchEncounterImpl encounter5 = buildEncounter("patientId2", "encounterType1");
        CouchEncounterImpl encounter6 = buildEncounter("patientId2", "encounterType2");

        allCouchEncounters.createOrUpdateEncounter(encounter);
        allCouchEncounters.createOrUpdateEncounter(encounter2);
        allCouchEncounters.createOrUpdateEncounter(encounter3);
        allCouchEncounters.createOrUpdateEncounter(encounter4);
        allCouchEncounters.createOrUpdateEncounter(encounter5);
        allCouchEncounters.createOrUpdateEncounter(encounter6);

        List<CouchEncounterImpl> retrievedEncounters = allCouchEncounters.findEncountersByMotechIdAndEncounterType(encounter.getPatientId(), encounter.getEncounterType());
        assertEquals(3, retrievedEncounters.size());
    }

    private CouchEncounterImpl buildEncounter(String patientId, String encounterType) {
        Set<String> obsIds = new HashSet<String>();
        obsIds.add("obs1");
        obsIds.add("obs2");
        obsIds.add("obs3");
        return new CouchEncounterImpl(UUID.randomUUID().toString(), "providerId", "creatorId", "facilityId", new DateTime(), obsIds, patientId, encounterType);
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllCouchEncountersImpl) allCouchEncounters).removeAll();
    }
}
