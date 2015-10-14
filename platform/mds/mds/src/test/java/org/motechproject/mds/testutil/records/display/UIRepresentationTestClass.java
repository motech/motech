package org.motechproject.mds.testutil.records.display;

import org.motechproject.mds.annotations.UIRepresentation;

public class UIRepresentationTestClass {

    private final long id;
    private final String val;

    public UIRepresentationTestClass(long id, String val) {
        this.id = id;
        this.val = val;
    }

    public long getId() {
        return id;
    }

    @UIRepresentation
    public String uiRep() {
        return val;
    }

    @Override
    public String toString() {
        return "Should never get used";
    }
}
