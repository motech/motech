package org.motechproject.couch.mrs.repository.impl;

import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchFacilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllCouchFacilitiesImpl extends MotechBaseRepository<CouchFacility> implements AllCouchFacilities {

    @Autowired
    protected AllCouchFacilitiesImpl(@Qualifier("couchFacilityDatabaseConnector") CouchDbConnector db) {
        super(CouchFacility.class, db);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_facilityId", map = "function(doc) { if (doc.type ==='Facility') { emit(doc.facilityId, doc._id); }}")
    public List<CouchFacility> findByFacilityId(String facilityId) {

        if (facilityId == null) {
            return null;
        }

        ViewQuery viewQuery = createQuery("by_facilityId").key(facilityId).includeDocs(true);
        return db.queryView(viewQuery, CouchFacility.class);
    }

    @Override
    public void addFacility(CouchFacility facility) throws MRSCouchException {

        if (facility.getFacilityId() == null) {
            throw new NullPointerException("Facility id cannot be null.");
        }

        List<CouchFacility> facilities = findByFacilityId(facility.getFacilityId());

        if (!facilities.isEmpty()) {
            CouchFacility couchFacility = facilities.get(0);
            updateFields(couchFacility, facility);
            update(couchFacility);
            return;
        }

        try {
            super.add(facility);
        } catch (IllegalArgumentException e) {
            throw new MRSCouchException(e.getMessage(), e);
        }
    }

    private void updateFields(CouchFacility couchFacility, CouchFacility facility) {
        couchFacility.setCountry(facility.getCountry());
        couchFacility.setCountyDistrict(facility.getCountyDistrict());
        couchFacility.setName(facility.getName());
        couchFacility.setRegion(facility.getRegion());
        couchFacility.setStateProvince(facility.getStateProvince());
    }

    @Override
    public List<CouchFacility> findAllFacilities() {
        return this.getAll();
    }

    @Override
    @View(name = "by_locationName", map = "function(doc) { if (doc.type ==='Facility') { emit(doc.name, doc._id); }}")
    public List<CouchFacility> findByLocationName(String locationName) {

        if (locationName == null) {
            return null;
        }

        ViewQuery viewQuery = createQuery("by_locationName").key(locationName).includeDocs(true);
        return db.queryView(viewQuery, CouchFacility.class);
    }
}
