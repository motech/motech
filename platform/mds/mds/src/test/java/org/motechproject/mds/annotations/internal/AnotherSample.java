package org.motechproject.mds.annotations.internal;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.RestIgnore;
import org.motechproject.mds.annotations.RestOperations;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.util.SecurityMode;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;

@Entity(recordHistory = false, nonEditable = true)
@RestOperations({})
@CrudEvents(CrudEventType.NONE)
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Access(value = SecurityMode.USERS, members = {"motech"})
public class AnotherSample {
    @RestIgnore
    private DateTime modificationDate;
    private int anotherInt;
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

    public boolean isRestIgnoredBoolean() {
        return restIgnoredBoolean;
    }

    public void setRestIgnoredBoolean(boolean restIgnoredBoolean) {
        this.restIgnoredBoolean = restIgnoredBoolean;
    }
}
