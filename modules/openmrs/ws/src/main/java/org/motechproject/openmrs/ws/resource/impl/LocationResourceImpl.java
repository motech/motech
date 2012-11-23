package org.motechproject.openmrs.ws.resource.impl;

import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.OpenMrsInstance;
import org.motechproject.openmrs.ws.RestClient;
import org.motechproject.openmrs.ws.resource.LocationResource;
import org.motechproject.openmrs.ws.resource.model.Location;
import org.motechproject.openmrs.ws.resource.model.LocationListResult;
import org.motechproject.openmrs.ws.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class LocationResourceImpl implements LocationResource {

    private final RestClient restClient;
    private final OpenMrsInstance openmrsInstance;

    @Autowired
    public LocationResourceImpl(RestClient restClient, OpenMrsInstance openmrsInstance) {
        this.restClient = restClient;
        this.openmrsInstance = openmrsInstance;
    }

    @Override
    public LocationListResult getAllLocations() throws HttpException {
        String json = restClient.getJson(openmrsInstance.toInstancePath("/location?v=full"));
        return (LocationListResult) JsonUtils.readJson(json, LocationListResult.class);
    }

    @Override
    public LocationListResult queryForLocationByName(String locationName) throws HttpException {
        String json = restClient.getJson(openmrsInstance.toInstancePathWithParams("/location?q={name}&v=full",
                locationName));
        return (LocationListResult) JsonUtils.readJson(json, LocationListResult.class);

    }

    @Override
    public Location getLocationById(String uuid) throws HttpException {
        String json = restClient.getJson(openmrsInstance.toInstancePathWithParams("/location/{uuid}", uuid));
        return (Location) JsonUtils.readJson(json, Location.class);
    }

    @Override
    public Location createLocation(Location location) throws HttpException {
        Gson gson = new GsonBuilder().create();
        String jsonRequest = gson.toJson(location);
        String jsonResponse = restClient.postForJson(openmrsInstance.toInstancePath("/location"), jsonRequest);
        return (Location) JsonUtils.readJson(jsonResponse, Location.class);
    }
}
