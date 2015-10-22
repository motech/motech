package org.motechproject.mds.test.domain.historytest;

import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsEntity;

import javax.jdo.annotations.Persistent;

/**
 * House and address are 1:!
 */
@Entity(recordHistory = true, maxFetchDepth = 2)
public class House extends MdsEntity {

    @Field
    private String name;

    @Field
    @Persistent(mappedBy = "house", defaultFetchGroup = "true")
    @Cascade(delete = true)
    private Address address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
