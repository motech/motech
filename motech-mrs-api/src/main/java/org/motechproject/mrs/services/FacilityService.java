package org.motechproject.mrs.services;

import java.util.List;

public interface FacilityService {
    Facility saveFacility(Facility facility);

    List<Facility> getFacilities();

    Facility getFacility(String name);
}
