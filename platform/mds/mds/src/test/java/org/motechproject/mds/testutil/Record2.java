package org.motechproject.mds.testutil;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;

import java.util.Date;
import java.util.Objects;

/**
 * Same as {@link org.motechproject.mds.testutil.records.Record}, but
 * the id field does not have a default value.
 */
@Entity(recordHistory = true)
public class Record2 {
    private Long id;
    private String creator;
    private String owner;
    private String modifiedBy;
    private DateTime creationDate;
    private DateTime modificationDate;

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

    public Date getDateIgnoredByRest() {
        return dateIgnoredByRest;
    }

    public void setDateIgnoredByRest(Date dateIgnoredByRest) {
        this.dateIgnoredByRest = dateIgnoredByRest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)  {
            return true;
        } else if (!(o instanceof Record2)) {
            return false;
        } else {
            Record2 that = (Record2) o;
            return Objects.equals(id, that.id) && Objects.equals(creator, that.creator) &&
                    Objects.equals(owner, that.owner) && Objects.equals(modifiedBy, that.modifiedBy) &&
                    Objects.equals(creationDate, that.creationDate) && Objects.equals(modificationDate, that.modificationDate)
                    && Objects.equals(value, that.value) && Objects.equals(date, that.date) &&
                    Objects.equals(dateIgnoredByRest, that.dateIgnoredByRest);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creator, owner, modifiedBy, creationDate, modificationDate, value, date,
                dateIgnoredByRest);
    }
}
