package org.motechproject.openmrs.services;

import org.motechproject.mrs.services.Location;
import org.motechproject.mrs.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;

public class LocationServiceImpl implements LocationService {
    @Autowired
    private org.openmrs.api.LocationService locationService;

    @Override
    public Location saveLocation(String name, String tag) {
        org.openmrs.Location location = new org.openmrs.Location();
        location.setName(name);
        this.locationService.saveLocation(location);
        return new Location(location.getName());
    }
}
