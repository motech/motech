package org.motechproject.mds.testutil.records;

public class Record {

    private Long id = 1L;
    private String value = "value";

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
}