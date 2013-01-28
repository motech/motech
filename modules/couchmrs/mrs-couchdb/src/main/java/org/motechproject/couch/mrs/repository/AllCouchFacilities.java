package org.motechproject.couch.mrs.repository;

import java.util.List;
import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.MRSCouchException;

public interface AllCouchFacilities {

    List<CouchFacility> findByFacilityId(String facilityId);

    void addFacility(CouchFacility facility) throws MRSCouchException;

    void update(CouchFacility facility);

    void remove(CouchFacility facility);

    List<CouchFacility> findAllFacilities();

    List<CouchFacility> findByLocationName(String locationName);

}
