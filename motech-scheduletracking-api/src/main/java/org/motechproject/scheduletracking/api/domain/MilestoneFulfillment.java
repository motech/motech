package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

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
        return fulfillmentDateTime;
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
