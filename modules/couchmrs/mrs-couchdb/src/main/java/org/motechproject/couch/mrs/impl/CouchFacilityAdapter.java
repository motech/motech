package org.motechproject.couch.mrs.impl;

import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchFacilities;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.mrs.services.FacilityAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouchFacilityAdapter implements FacilityAdapter {

    @Autowired
    private AllCouchFacilities allFacilities;

    @Autowired
    private EventRelay eventRelay;

    @Override
    public Facility saveFacility(Facility facility) {

        try {
            allFacilities.addFacility((CouchFacility) facility);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_FACILITY_SUBJECT, EventHelper.facilityParameters(facility)));
        } catch (MRSCouchException e) {
            return null;
        }

        return facility;
    }

    @Override
    public List<CouchFacility> getFacilities() {
        return allFacilities.findAllFacilities();
    }

    @Override
    public List<CouchFacility> getFacilities(String locationName) {
        return allFacilities.findByLocationName(locationName);
    }

    @Override
    public Facility getFacility(String facilityId) {

        List<CouchFacility> facilities = allFacilities.findByFacilityId(facilityId);

        if (facilities != null && facilities.size() > 0) {
            return facilities.get(0);
        }

        return null;
    }
}
