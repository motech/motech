package org.motechproject.mds.testJdoDiscriminator.domain;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class Discr2CaseA extends Discr2 {

    public Discr2CaseA(Integer kilograms) {
        this.kilograms = kilograms;
    }

    @Field
    private Integer kilograms;

    public Integer getKilograms() {
        return kilograms;
    }

    public void setKilograms(Integer kilograms) {
        this.kilograms = kilograms;
    }
}
