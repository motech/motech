package org.motechproject.mds.test.domain.historytest;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.SingleRelationshipDisplay;
import org.motechproject.mds.domain.MdsEntity;

/**
 * House and address are 1:1
 */
@Entity(recordHistory = true)
public class Address extends MdsEntity {

    @Field
    private String street;

    @Field
    @SingleRelationshipDisplay(allowAddingNew = false)
    private House house;

    public String getStreet() {
        return street;
    }

    public void setStreet(String mask) {
        this.street = mask;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }
}
