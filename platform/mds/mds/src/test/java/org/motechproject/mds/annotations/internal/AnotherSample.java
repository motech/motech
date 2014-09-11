package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.RestOperations;

@Entity
@RestOperations({})
public class AnotherSample {
    private int anotherInt;

    public AnotherSample(int anotherInt) {
        this.anotherInt = anotherInt;
    }

    public int getAnotherInt() {
        return anotherInt;
    }

    public void setAnotherInt(int anotherInt) {
        this.anotherInt = anotherInt;
    }
}
