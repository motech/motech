package org.motechproject.mds.test.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class Discr1CaseB extends Discr1 {

    public Discr1CaseB(Integer centimeters) {
        this.centimeters = centimeters;
    }

    @Field
    private Integer centimeters;

    public Integer getCentimeters() {
        return centimeters;
    }

    public void setCentimeters(Integer centimeters) {
        this.centimeters = centimeters;
    }
}
