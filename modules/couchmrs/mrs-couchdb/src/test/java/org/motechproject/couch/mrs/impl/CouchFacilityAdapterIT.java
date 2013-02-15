package org.motechproject.couch.mrs.impl;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchFacilities;
import org.motechproject.couch.mrs.repository.impl.AllCouchFacilitiesImpl;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class CouchFacilityAdapterIT extends SpringIntegrationTest {

    @Autowired
    private CouchFacilityAdapter facilityAdapter;

    @Autowired
    private AllCouchFacilities allFacilities;


    @Autowired
    @Qualifier("couchPatientDatabaseConnector")
    CouchDbConnector connector;

    @Test
    public void shouldSaveAFacilityAndRetrieveByName() throws MRSCouchException {

        CouchFacility facility = new CouchFacility();
        facility.setFacilityId("facilityId");
        facility.setName("facilityName");

        CouchFacility facility2 = new CouchFacility();
        facility2.setFacilityId("facilityId2");
        facility2.setName("facilityName");

        facilityAdapter.saveFacility(facility);
        facilityAdapter.saveFacility(facility2);

        List<CouchFacility> facilities = facilityAdapter.getFacilities("facilityName");

        assertEquals(asList("facilityId", "facilityId2"), extract(facilities, on(CouchFacility.class).getFacilityId()));
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllCouchFacilitiesImpl) allFacilities).removeAll();
    }
}
