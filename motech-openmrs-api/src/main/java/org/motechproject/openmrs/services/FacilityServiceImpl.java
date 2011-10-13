package org.motechproject.openmrs.services;

import org.motechproject.mrs.services.Facility;
import org.motechproject.mrs.services.FacilityService;
import org.openmrs.Location;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class FacilityServiceImpl implements FacilityService {
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
        return new Facility(String.valueOf(savedLocation.getId()), savedLocation.getName(), savedLocation.getCountry(),
                savedLocation.getAddress6(), savedLocation.getCountyDistrict(), savedLocation.getStateProvince());
    }

    @Override
    public List<Facility> getFacilities() {
        List<Location> locations = locationService.getAllLocations();
        List<Facility> facilities = new ArrayList<Facility>();
        for (Location location : locations) {
            facilities.add(new Facility(String.valueOf(location.getId()), location.getName(),
                    location.getCountry(), location.getAddress6(), location.getCountyDistrict(), location.getStateProvince()));
        }
        return facilities;
    }

    @Override
    public Facility getFacility(String name) {
        final Location location = locationService.getLocation(name);
        if(location != null)
        return new Facility(String.valueOf(location.getId()), location.getName(),
                    location.getCountry(), location.getAddress6(), location.getCountyDistrict(), location.getStateProvince());
        return null;
    }
}
