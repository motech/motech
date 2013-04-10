package org.motechproject.couch.mrs.impl;

import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchFacilities;
import org.motechproject.couch.mrs.util.CouchMRSConverterUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouchFacilityAdapter implements MRSFacilityAdapter {

    @Autowired
    private AllCouchFacilities allFacilities;

    @Autowired
    private EventRelay eventRelay;

    @Override
    public MRSFacility saveFacility(MRSFacility facility) {

        CouchFacility convertedFacility = CouchMRSConverterUtil.convertFacilityToCouchFacility(facility);

        try {
            allFacilities.addFacility(convertedFacility);
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
    public MRSFacility getFacility(String facilityId) {

        List<CouchFacility> facilities = allFacilities.findByFacilityId(facilityId);

        if (facilities != null && facilities.size() > 0) {
            return facilities.get(0);
        }

        return null;
    }

    @Override
    public void deleteFacility(String facilityId) {
        CouchFacility facility = (CouchFacility) getFacility(facilityId);
        if (facility != null) {
            allFacilities.remove(facility);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.DELETED_FACILITY_SUBJECT, EventHelper.facilityParameters(facility)));
        }
    }

    @Override
    public MRSFacility updateFacility(MRSFacility facility) {
        CouchFacility facilityToUpdate = (CouchFacility) getFacility(facility.getFacilityId());
        if (facilityToUpdate != null) {
            facilityToUpdate.setCountry(facility.getCountry());
            facilityToUpdate.setCountyDistrict(facility.getCountyDistrict());
            facilityToUpdate.setName(facility.getName());
            facilityToUpdate.setRegion(facility.getRegion());
            facilityToUpdate.setStateProvince(facility.getStateProvince());
            allFacilities.update(facilityToUpdate);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.UPDATED_FACILITY_SUBJECT, EventHelper.facilityParameters(facilityToUpdate)));
            return getFacility(facilityToUpdate.getFacilityId());
        } else {
            return null;
        }
    }
}
