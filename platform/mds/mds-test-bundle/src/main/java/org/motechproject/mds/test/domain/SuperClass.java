package org.motechproject.mds.test.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public abstract class SuperClass {

    @Field
    private String superClassString;

    public SuperClass (String superClassString) {
        this.superClassString = superClassString;
    }

    public String getSuperClassString() {
        return superClassString;
    }

    public void setSuperClassString(String superClassString) {
        this.superClassString = superClassString;
    }
}
