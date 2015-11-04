package org.motechproject.email.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class Test_Task_Trigger {

    @Field
    private Boolean changeMe;

    public Boolean getChangeMe() {
        return changeMe;
    }

    public void setChangeMe(Boolean changeMe) {
        this.changeMe = changeMe;
    }
}
