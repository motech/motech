package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

import static org.motechproject.commons.date.util.DateUtil.setTimeZone;

public class MilestoneFulfillment implements Serializable {
    private static final long serialVersionUID = 7990212996988800913L;
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
