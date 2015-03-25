package org.motechproject.mds.annotations.internal;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.RestIgnore;
import org.motechproject.mds.annotations.RestOperations;
import org.motechproject.mds.event.CrudEventType;

@Entity(recordHistory = false)
@RestOperations({})
@CrudEvents(CrudEventType.NONE)
public class AnotherSample {

    @Field
    @RestIgnore
    private DateTime modificationDate;

    @Field
    private int anotherInt;

    @Field
    private String anotherString;

    @RestIgnore
    private boolean restIgnoredBoolean;

    public AnotherSample(int anotherInt) {
        this.anotherInt = anotherInt;
    }

    public int getAnotherInt() {
        return anotherInt;
    }

    public void setAnotherInt(int anotherInt) {
        this.anotherInt = anotherInt;
    }

    public DateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(DateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getAnotherString() {
        return anotherString;
    }

    public void setAnotherString(String anotherString) {
        this.anotherString = anotherString;
    }

    @Field
    public boolean isRestIgnoredBoolean() {
        return restIgnoredBoolean;
    }

    public void setRestIgnoredBoolean(boolean restIgnoredBoolean) {
        this.restIgnoredBoolean = restIgnoredBoolean;
    }
}
