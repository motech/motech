package org.motechproject.mds.web.rest;

import java.util.Objects;

public class TestRecord {

    private String name;
    private int val;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public TestRecord() {
    }

    public TestRecord(String name, int val) {
        this.name = name;
        this.val = val;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TestRecord)) {
            return false;
        }
        TestRecord other = (TestRecord) obj;
        return Objects.equals(name, other.name) && Objects.equals(val, other.val);
    }
}
