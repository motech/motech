package org.motechproject.mds.test.domain.revertFromTrash;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsEntity;

@Entity
public class HomeAddress extends MdsEntity {
    @Field
    private String street;

    @Field
    private String city;

    public HomeAddress(String street, String city) { this.street = street; this.city = city; }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
