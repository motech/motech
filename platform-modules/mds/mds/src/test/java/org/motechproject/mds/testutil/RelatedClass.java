package org.motechproject.mds.testutil;

import org.motechproject.mds.annotations.Entity;

/**
 * Class used in the entity builder test
 */
@Entity(recordHistory = true)
public class RelatedClass {

    private Integer intField;

    public Integer getIntField() {
        return intField;
    }

    public void setIntField(Integer intField) {
        this.intField = intField;
    }
}
