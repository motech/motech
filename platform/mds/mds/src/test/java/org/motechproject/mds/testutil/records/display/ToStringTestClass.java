package org.motechproject.mds.testutil.records.display;

public class ToStringTestClass {

    private final long id;
    private final String val;

    public ToStringTestClass(long id, String val) {
        this.id = id;
        this.val = val;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return val;
    }
}
