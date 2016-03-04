package org.motechproject.mds.test.secondary.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

public class DeserializationTestClass implements Serializable {

    private static final long serialVersionUID = -1305725172456062330L;

    private String name;

    private Long number;

    private DateTime someDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public DateTime getSomeDate() {
        return someDate;
    }

    public void setSomeDate(DateTime someDate) {
        this.someDate = someDate;
    }
}
