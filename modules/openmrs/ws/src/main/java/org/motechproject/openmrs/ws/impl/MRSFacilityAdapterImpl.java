package org.motechproject.openmrs.ws.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.model.OpenMRSFacility;
import org.motechproject.mrs.services.FacilityAdapter;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.LocationResource;
import org.motechproject.openmrs.ws.resource.model.Location;
import org.motechproject.openmrs.ws.resource.model.LocationListResult;
import org.motechproject.openmrs.ws.util.ConverterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("facilityAdapter")
public class MRSFacilityAdapterImpl implements FacilityAdapter {
    private static final Logger LOGGER = Logger.getLogger(MRSFacilityAdapterImpl.class);

    private final LocationResource locationResource;

    @Autowired
    public MRSFacilityAdapterImpl(LocationResource locationResource) {
        this.locationResource = locationResource;
    }

    @Override
    public List<OpenMRSFacility> getFacilities() {
        LocationListResult result = null;
        try {
            result = locationResource.getAllLocations();
        } catch (HttpException e) {
            LOGGER.error("Failed to retrieve all facilities");
            return Collections.emptyList();
        }

        return mapLocationToMrsFacility(result.getResults());
    }

    private List<OpenMRSFacility> mapLocationToMrsFacility(List<Location> facilities) {
        List<OpenMRSFacility> mrsFacilities = new ArrayList<OpenMRSFacility>();
        for (Location location : facilities) {
            mrsFacilities.add(ConverterUtils.convertLocationToMrsLocation(location));
        }
        return mrsFacilities;
    }

    @Override
    public List<OpenMRSFacility> getFacilities(String locationName) {
        Validate.notEmpty(locationName, "Location name cannot be empty");
        LocationListResult result = null;
        try {
            result = locationResource.queryForLocationByName(locationName);
        } catch (HttpException e) {
            LOGGER.error("Failed to retrieve all facilities by location name: " + locationName);
            return Collections.emptyList();
        }

        return mapLocationToMrsFacility(result.getResults());
    }

    @Override
    public OpenMRSFacility getFacility(String facilityId) {
        Validate.notEmpty(facilityId, "Facility id cannot be empty");
        Location location = null;
        try {
            location = locationResource.getLocationById(facilityId);
        } catch (HttpException e) {
            LOGGER.error("Failed to fetch information about location with uuid: " + facilityId);
            return null;
        }

        return ConverterUtils.convertLocationToMrsLocation(location);
    }

    @Override
    public OpenMRSFacility saveFacility(Facility facility) {
        Validate.notNull(facility, "Facility cannot be null");

        // The uuid cannot be included with the request, otherwise OpenMRS will
        // fail
        facility.setFacilityId(null);
        Location location = convertMrsFacilityToLocation(facility);
        Location saved = null;
        try {
            saved = locationResource.createLocation(location);
        } catch (HttpException e) {
            LOGGER.error("Could not create location with name: " + location.getName());
            return null;
        }

        return ConverterUtils.convertLocationToMrsLocation(saved);
    }

    private Location convertMrsFacilityToLocation(Facility facility) {
        Location location = new Location();
        location.setAddress6(facility.getRegion());
        location.setDescription(facility.getName());
        location.setCountry(facility.getCountry());
        location.setCountyDistrict(facility.getCountyDistrict());
        location.setName(facility.getName());
        location.setStateProvince(facility.getStateProvince());
        location.setUuid(facility.getFacilityId());
        return location;
    }
}
