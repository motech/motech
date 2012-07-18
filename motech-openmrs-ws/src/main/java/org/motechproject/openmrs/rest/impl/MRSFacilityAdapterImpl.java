package org.motechproject.openmrs.rest.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestClient;
import org.motechproject.openmrs.rest.model.Location;
import org.motechproject.openmrs.rest.model.LocationListResult;
import org.motechproject.openmrs.rest.util.ConverterUtils;
import org.motechproject.openmrs.rest.util.JsonUtils;
import org.motechproject.openmrs.rest.util.OpenMrsUrlHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component("facilityAdapter")
public class MRSFacilityAdapterImpl implements MRSFacilityAdapter {
    private static final Logger LOGGER = Logger.getLogger(MRSFacilityAdapterImpl.class);

    private final RestClient restClient;
    private final OpenMrsUrlHolder urlHolder;

    @Autowired
    public MRSFacilityAdapterImpl(RestClient restClient, OpenMrsUrlHolder urlHolder) {
        this.restClient = restClient;
        this.urlHolder = urlHolder;
    }

    @Override
    public List<MRSFacility> getFacilities() {
        try {
            String json = restClient.getJson(urlHolder.getFacilityListUri());
            return getLocationListFromJson(json);
        } catch (HttpException e) {
            LOGGER.error("Failed to retrieve all facilities");
            throw new MRSException(e);
        }
    }

    private List<MRSFacility> getLocationListFromJson(String json) {
        LocationListResult result = readLocationListResult(json);
        return mapLocationToMrsFacility(result.getResults());
    }

    private LocationListResult readLocationListResult(String json) {
        LocationListResult result = (LocationListResult) JsonUtils.readJson(json, LocationListResult.class);
        return result;
    }

    private List<MRSFacility> mapLocationToMrsFacility(List<Location> facilities) {
        List<MRSFacility> mrsFacilities = new ArrayList<MRSFacility>();
        for (Location location : facilities) {
            mrsFacilities.add(ConverterUtils.convertLocationToMrsLocation(location));
        }
        return mrsFacilities;
    }

    @Override
    public List<MRSFacility> getFacilities(String locationName) {
        Validate.notEmpty(locationName, "Location name cannot be empty");
        try {
            String json = restClient.getJson(urlHolder.getFacilityListUri(locationName));
            return getLocationListFromJson(json);
        } catch (HttpException e) {
            LOGGER.error("Failed to retrieve all facilities by location name: " + locationName);
            throw new MRSException(e);
        }
    }

    @Override
    public MRSFacility getFacility(String facilityId) {
        Validate.notEmpty(facilityId, "Facility id cannot be empty");
        try {
            String json = restClient.getJson(urlHolder.getFacilityFindUri(facilityId));
            Location location = readLocation(json);
            return ConverterUtils.convertLocationToMrsLocation(location);
        } catch (HttpException e) {
            LOGGER.error("Failed to fetch information about facility id: " + facilityId);
            throw new MRSException(e);
        }
    }

    private Location readLocation(String json) {
        Location location = (Location) JsonUtils.readJson(json, Location.class);
        return location;
    }

    @Override
    public MRSFacility saveFacility(MRSFacility facility) {
        Validate.notNull(facility, "Facility cannot be null");

        // The uuid cannot be included with the request, otherwise OpenMRS will
        // fail
        facility.setId(null);

        try {
            Gson gson = new GsonBuilder().create();
            String jsonRequest = gson.toJson(convertMrsFacilityToLocation(facility));
            String jsonResponse = restClient.postForJson(urlHolder.getFacilityCreateUri(), jsonRequest);
            return ConverterUtils.convertLocationToMrsLocation(readLocation(jsonResponse));
        } catch (HttpException e) {
            LOGGER.error("Could not create facility with name: " + facility.getName());
            throw new MRSException(e);
        }
    }

    private Location convertMrsFacilityToLocation(MRSFacility facility) {
        Location location = new Location();
        location.setAddress6(facility.getRegion());
        location.setCountry(facility.getCountry());
        location.setCountyDistrict(facility.getCountyDistrict());
        location.setName(facility.getName());
        location.setStateProvince(facility.getStateProvince());
        location.setUuid(facility.getId());
        return location;
    }
}
