package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

import static org.motechproject.util.DateUtil.setTimeZone;

public class MilestoneFulfillment implements Serializable {
    private String milestoneName;
    private DateTime fulfillmentDateTime;

    private MilestoneFulfillment() {
    }

    public MilestoneFulfillment(String milestoneName, DateTime fulfillmentDateTime) {
        this.milestoneName = milestoneName;
        this.fulfillmentDateTime = fulfillmentDateTime;
    }

    public DateTime getFulfillmentDateTime() {
        return setTimeZone(fulfillmentDateTime);
    }

    public void setFulfillmentDateTime(DateTime fulfillmentDateTime) {
        this.fulfillmentDateTime = fulfillmentDateTime;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    public void setMilestoneName(String milestoneName) {
        this.milestoneName = milestoneName;
    }
}
