package org.motechproject.openmrs.ws.resource;

import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.Location;
import org.motechproject.openmrs.ws.resource.model.LocationListResult;

public interface LocationResource {

    LocationListResult getAllLocations() throws HttpException;

    LocationListResult queryForLocationByName(String locationName) throws HttpException;

    Location getLocationById(String uuid) throws HttpException;

    Location createLocation(Location location) throws HttpException;

}
