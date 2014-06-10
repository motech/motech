package org.motechproject.mds.testutil.records;

import org.motechproject.mds.annotations.Entity;

import java.util.Date;

@Entity
public class Record {

    private Long id = 1L;
    private String value = "value";
    private Date date = new Date();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}