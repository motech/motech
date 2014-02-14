package org.motechproject.mds.annotations.internal;

import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.annotations.UIDisplayable;
import org.motechproject.mds.annotations.InSet;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.NotInSet;
import org.motechproject.mds.annotations.UIFilterable;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class Sample {
    // if you added a new field (and it has no @Ignore annotation) please increase this number.
    public static final long FIELD_COUNT = 10;

    // test class

    @Field
    @UIFilterable
    private Boolean world;

    @UIFilterable
    @DecimalMin(value = "3")
    @DecimalMax(value = "4")
    @InSet(value = {"3", "3.14", "4"})
    @NotInSet(value = {"1", "2", "5"})
    public Integer pi;

    @DecimalMax(value = "1")
    @DecimalMin(value = "0")
    @InSet(value = {"1", "0.75", "0.5", "0.25", "0"})
    @NotInSet(value = {"-1", "2", "3"})
    public Double epsilon;

    @Min(value = 0)
    @Max(value = 10)
    public Integer random;

    @Max(value = 1)
    @Min(value = 0)
    public Double gaussian;

    @Pattern(regexp = "[A-Z][a-z]{9}")
    @Size(min = 10, max = 20)
    public String poem;

    @DecimalMin(value = "100")
    @DecimalMax(value = "500")
    public String article;

    @Ignore
    public String ignored;

    @UIDisplayable
    public Double money;

    private String ignoredPrivate;

    private Date serverDate;

    private Time localTime;

    @Lookup
    public void lookupTest() {

    }

    public Boolean getWorld() {
        return world;
    }

    public void setWorld(Boolean world) {
        this.world = world;
    }

    @Field(displayName = "Server Date")
    @UIFilterable
    public Date getServerDate() {
        return serverDate;
    }

    public void setServerDate(Date serverDate) {
        this.serverDate = serverDate;
    }

    public Time getLocalTime() {
        return localTime;
    }

    @Field(required = true)
    public void setLocalTime(Time localTime) {
        this.localTime = localTime;
    }

    @Ignore
    public void setIgnoredPrivate(String ignoredPrivate) {
        this.ignoredPrivate = ignoredPrivate;
    }

    @Ignore
    public String getIgnoredPrivate() {
        return ignoredPrivate;
    }
}
