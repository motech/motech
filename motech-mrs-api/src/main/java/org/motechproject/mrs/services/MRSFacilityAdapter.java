package org.motechproject.mrs.services;

import org.motechproject.mrs.model.MRSFacility;

import java.util.List;

/**
 * Interface to save and get facilities (Location)
 */
public interface MRSFacilityAdapter {
    /**
     *  Saves the given facility in the MRS System
     * @param facility  object to be saved
     * @return The saved instance of the facility
     */
    MRSFacility saveFacility(MRSFacility facility);

    /**
     * Gets all the facilities in the MRS system
     * @return List of all available facilities
     */
    List<MRSFacility> getFacilities();

    /**
     * Fetches all facilities that have the given location name
     * @param locationName Value to be used to search
     * @return List of matched facilities
     */
    List<MRSFacility> getFacilities(String locationName);

    /**
     * Fetches facility by facility id (not the MOTECH ID of the facility)
     * @param facilityId Id of the facility to be fetched
     * @return Facility with the given id
     */
    MRSFacility getFacility(String facilityId);
}
