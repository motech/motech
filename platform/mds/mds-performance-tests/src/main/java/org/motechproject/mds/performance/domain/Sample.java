package org.motechproject.mds.performance.domain;


import org.motechproject.mds.annotations.Entity;

@Entity
public class Sample {
    private Integer testInt;
    private String testString;

    public Sample() {
    }

    public Sample(Integer testInt, String testString) {
        this.testInt = testInt;
        this.testString = testString;
    }

    public Integer getTestInt() {
        return testInt;
    }

    public void setTestInt(Integer testInt) {
        this.testInt = testInt;
    }

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }
}
