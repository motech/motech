package org.motechproject.mrs.model;

import java.util.Objects;

import org.motechproject.mrs.domain.Facility;

/**
 * Maintains details about the facility
 */
public class OpenMRSFacility implements Facility {
    private String id;

    private String name;
    private String country;
    private String region;
    private String countyDistrict;
    private String stateProvince;

    /**
     * Creates a facility with the given facility id
     * @param id Facility id
     */
    public OpenMRSFacility(String id) {
        this.id = id;
    }

    /**
     * Creates a facility object with the given details
     * @param name  Name of the facility
     * @param country Name of the country
     * @param region Name of the region
     * @param countyDistrict Name of the county/District
     * @param stateProvince Name of the State/Province
     */
    public OpenMRSFacility(String name, String country, String region, String countyDistrict, String stateProvince) {
        this.name = name;
        this.country = country;
        this.region = region;
        this.countyDistrict = countyDistrict;
        this.stateProvince = stateProvince;
    }

    /**
     * Creates a facility object with the given details
     * @param id Facility id
     * @param name  Name of the facility
     * @param country Name of the country
     * @param region Name of the region
     * @param countyDistrict Name of the county/District
     * @param stateProvince Name of the State/Province
     */
    public OpenMRSFacility(String id, String name, String country, String region, String countyDistrict, String stateProvince) {
        this(name, country, region, countyDistrict, stateProvince);
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getCountyDistrict() {
        return countyDistrict;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setCountyDistrict(String countyDistrict) {
        this.countyDistrict = countyDistrict;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OpenMRSFacility)) {
            return false;
        }

        OpenMRSFacility facility = (OpenMRSFacility) o;

        return Objects.equals(country, facility.country) && Objects.equals(countyDistrict, facility.countyDistrict) &&
                Objects.equals(id, facility.id) && Objects.equals(name, facility.name) &&
                Objects.equals(region, facility.region) && Objects.equals(stateProvince, facility.stateProvince);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (countyDistrict != null ? countyDistrict.hashCode() : 0);
        result = 31 * result + (stateProvince != null ? stateProvince.hashCode() : 0);
        return result;
    }

    @Override
    public String getFacilityId() {
        return id;
    }

    @Override
    public void setFacilityId(String id) {
        this.id = id;
    }
}
