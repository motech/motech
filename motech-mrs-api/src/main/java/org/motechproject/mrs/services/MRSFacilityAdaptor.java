package org.motechproject.mrs.services;

import org.motechproject.mrs.model.Facility;

import java.util.List;

public interface MRSFacilityAdaptor {
    Facility saveFacility(Facility facility);

    List<Facility> getFacilities();

    List<Facility> getFacilities(String name);
}
