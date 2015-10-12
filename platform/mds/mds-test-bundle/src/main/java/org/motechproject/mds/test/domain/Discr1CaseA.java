package org.motechproject.mds.test.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class Discr1CaseA extends Discr1 {

    public Discr1CaseA(Integer kilograms) {
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
