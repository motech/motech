package org.motechproject.couch.mrs.model;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.mrs.domain.Facility;

@TypeDiscriminator("doc.type === 'Facility'")
public class CouchFacility extends MotechBaseDataObject implements Facility {

    private static final long serialVersionUID = 1L;

    private final String type = "Facility";

    private String externalId;
    private String name;
    private String country;
    private String region;
    private String countyDistrict;
    private String stateProvince;

    public CouchFacility() {
        super();
        this.setType(type);
    }

    public CouchFacility(String facilityId) {
        this();
        this.externalId = facilityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountyDistrict() {
        return countyDistrict;
    }

    public void setCountyDistrict(String countyDistrict) {
        this.countyDistrict = countyDistrict;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    @Override
    public String getFacilityId() {
        return externalId;
    }

    @Override
    public void setFacilityId(String id) {
        this.externalId = id;
    }

}
