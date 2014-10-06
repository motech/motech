package org.motechproject.mds.testutil.records;

public class RelatedRecord {

    private String name;
    private Integer val;
    private Long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RelatedRecord() {
    }

    public RelatedRecord(String name, Integer val, Long id) {
        this.name = name;
        this.val = val;
        this.id = id;
    }
}
