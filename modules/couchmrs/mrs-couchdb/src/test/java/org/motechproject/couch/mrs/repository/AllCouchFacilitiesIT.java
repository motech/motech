package org.motechproject.couch.mrs.repository;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.impl.AllCouchFacilitiesImpl;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class AllCouchFacilitiesIT extends SpringIntegrationTest {

    @Autowired
    private AllCouchFacilities allCouchFacilities;

    @Autowired
    @Qualifier("couchFacilityDatabaseConnector")
    private CouchDbConnector connector;

    @Test
    public void shouldSaveAFacilityAndRetrieveById() throws MRSCouchException {
        CouchFacility facility = new CouchFacility("123");
        facility.setCountry("US");

        allCouchFacilities.addFacility(facility);

        List<CouchFacility> facilitiesRetrieved = allCouchFacilities.findByFacilityId("123");

        CouchFacility facilityRetrieved = facilitiesRetrieved.get(0);

        assertEquals(facilityRetrieved.getCountry(), "US");
        assertEquals(facilityRetrieved.getFacilityId(), "123");
    }

    @Test
    public void shouldUpdateFacilityRecord() throws MRSCouchException {
        CouchFacility facility1 = new CouchFacility("123");
        facility1.setCountry("US");

        allCouchFacilities.addFacility(facility1);

        CouchFacility facility2 = new CouchFacility("123");
        facility2.setCountry("Ethiopia");

        allCouchFacilities.addFacility(facility2);

        List<CouchFacility> facilitiesRetrieved = allCouchFacilities.findByFacilityId("123");

        CouchFacility facilityRetrieved = facilitiesRetrieved.get(0);

        assertEquals(facilityRetrieved.getCountry(), "Ethiopia");
        assertEquals(facilityRetrieved.getFacilityId(), "123");
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllCouchFacilitiesImpl) allCouchFacilities).removeAll();
    }
}
