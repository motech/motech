package org.motechproject.mds.testutil.records;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;

import java.util.Date;
import java.util.List;

@Entity(recordHistory = true)
public class Record {
    private Long id = 1L;
    private String creator;
    private String owner;
    private String modifiedBy;
    private DateTime creationDate;
    private DateTime modificationDate;
    private List<RelatedRecord> relatedValues;
    private RelatedRecord relatedSingleValue;

    private String value = "value";
    private Date date = new Date();
    private Date dateIgnoredByRest;

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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public DateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(DateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public List<RelatedRecord> getRelatedValues() {
        return relatedValues;
    }

    public void setRelatedValues(List<RelatedRecord> relatedValues) {
        this.relatedValues = relatedValues;
    }

    public RelatedRecord getRelatedSingleValue() {
        return relatedSingleValue;
    }

    public void setRelatedSingleValue(RelatedRecord relatedSingleValue) {
        this.relatedSingleValue = relatedSingleValue;
    }

    public Date getDateIgnoredByRest() {
        return dateIgnoredByRest;
    }

    public void setDateIgnoredByRest(Date dateIgnoredByRest) {
        this.dateIgnoredByRest = dateIgnoredByRest;
    }
}
