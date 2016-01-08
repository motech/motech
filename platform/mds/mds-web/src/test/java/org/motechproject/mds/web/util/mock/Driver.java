package org.motechproject.mds.web.util.mock;

import org.joda.time.DateTime;

public class Driver {

    private String name;
    private DateTime dateOfBirth;

    public Driver(String name, DateTime dateOfBirth) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
    }

    public DateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
