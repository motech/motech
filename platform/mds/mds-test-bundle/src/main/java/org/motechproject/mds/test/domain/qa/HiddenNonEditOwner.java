package org.motechproject.mds.test.domain.qa;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.NonEditable;

/**
 * This class is used for testing MOTECH-2178.
 * It has a hidden non-editable owner field. It should be possible to edit these
 * entities through the data browser without issues.
 */
@Entity
public class HiddenNonEditOwner {

    @Field
    private String regularField;

    @Field
    @NonEditable(display = false)
    private String owner;

    public String getRegularField() {
        return regularField;
    }

    public void setRegularField(String regularField) {
        this.regularField = regularField;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
