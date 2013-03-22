package org.motechproject.couch.mrs.repository;

import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.MRSCouchException;

import java.util.List;

public interface AllCouchFacilities {

    List<CouchFacility> findByFacilityId(String facilityId);

    void addFacility(CouchFacility facility) throws MRSCouchException;

    void update(CouchFacility facility);

    void remove(CouchFacility facility);

    List<CouchFacility> findAllFacilities();

    List<CouchFacility> findByLocationName(String locationName);

}
