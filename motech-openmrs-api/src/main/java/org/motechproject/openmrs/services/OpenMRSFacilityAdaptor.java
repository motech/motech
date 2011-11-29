package org.motechproject.openmrs.services;

import org.motechproject.mrs.model.Facility;
import org.motechproject.mrs.services.MRSFacilityAdaptor;
import org.openmrs.Location;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class OpenMRSFacilityAdaptor implements MRSFacilityAdaptor {
    @Autowired
    private org.openmrs.api.LocationService locationService;

    @Override
    public Facility saveFacility(Facility facility) {
        Location location = new Location();
        location.setName(facility.getName());
        location.setCountry(facility.getCountry());
        location.setAddress6(facility.getRegion());
        location.setStateProvince(facility.getStateProvince());
        location.setCountyDistrict(facility.getCountyDistrict());

        Location savedLocation = this.locationService.saveLocation(location);
        return createFacility(savedLocation);
    }

    @Override
    public List<Facility> getFacilities() {
        List<Location> locations = locationService.getAllLocations();
        List<Facility> facilities = new ArrayList<Facility>();
        for (Location location : locations) {
            facilities.add(createFacility(location));
        }
        return facilities;
    }

    @Override
    public List<Facility> getFacilities(String locationName) {
        final List<Location> locations = locationService.getLocations(locationName);
        final ArrayList<Facility> facilities = new ArrayList<Facility>();
        for (Location location : locations) {
            facilities.add(createFacility(location));
        }
        return facilities;
    }

    @Override
    public Facility getFacility(Integer facilityId) {
        return createFacility(getLocation(facilityId));
    }

    public Location getLocation(Integer facilityId) {
        return locationService.getLocation(facilityId);
    }

    public Facility createFacility(Location savedLocation) {
        return new Facility(String.valueOf(savedLocation.getId()), savedLocation.getName(), savedLocation.getCountry(),
                savedLocation.getAddress6(), savedLocation.getCountyDistrict(), savedLocation.getStateProvince());
    }
}
