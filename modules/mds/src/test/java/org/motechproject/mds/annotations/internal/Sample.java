package org.motechproject.mds.annotations.internal;

import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.UIFilterable;

import java.util.Date;

@Entity
public class Sample {
    // test class

    @Field
    @UIFilterable
    private Boolean world;

    @UIFilterable
    public Integer pi;

    @Ignore
    public String ignored;

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
