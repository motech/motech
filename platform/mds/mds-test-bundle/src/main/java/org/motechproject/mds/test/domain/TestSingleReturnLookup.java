package org.motechproject.mds.test.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class TestSingleReturnLookup {

    @Field
    private String firstFieldName;
    @Field
    private String secondFieldName;

    public TestSingleReturnLookup(String firstFieldName, String secondFieldName) {
        this.firstFieldName = firstFieldName;
        this.secondFieldName = secondFieldName;
    }

    public String getFirstFieldName() {
        return firstFieldName;
    }

    public void setFirstFieldName(String firstFieldName) {
        this.firstFieldName = firstFieldName;
    }

    public String getSecondFieldName() {
        return secondFieldName;
    }

    public void setSecondFieldName(String secondFieldName) {
        this.secondFieldName = secondFieldName;
    }
}
