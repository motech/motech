package org.motechproject.mds.testJdoDiscriminator.domain;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class Discr2CaseB extends Discr2 {

    public Discr2CaseB(Integer centimeters) {
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
