package org.motechproject.couch.mrs.repository;

import org.motechproject.couch.mrs.model.CouchFacility;

import java.util.List;

public interface AllCouchFacilities {

    List<CouchFacility> findByFacilityId(String facilityId);

    void addFacility(CouchFacility facility);

    void update(CouchFacility facility);

    void remove(CouchFacility facility);

    List<CouchFacility> findAllFacilities();

    List<CouchFacility> findByLocationName(String locationName);

}
