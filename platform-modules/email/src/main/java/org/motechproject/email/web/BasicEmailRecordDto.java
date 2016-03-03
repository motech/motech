package org.motechproject.email.web;

import org.joda.time.format.DateTimeFormat;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.email.domain.EmailRecord;

import java.util.Objects;

/**
 * The <code>BasicEmailRecordDto</code> class represents a single Email record, as it's seen by user
 * with basic email logging rights.
 */

public class BasicEmailRecordDto {

    private String deliveryTime;
    private String deliveryStatus;

    public BasicEmailRecordDto() {
        this.deliveryStatus = null;
        this.deliveryTime = null;
    }

    public BasicEmailRecordDto(EmailRecord record) {
        this.deliveryStatus = record.getDeliveryStatus().toString();
        this.deliveryTime = DateTimeFormat.forPattern("Y-MM-dd hh:mm:ss").print(DateUtil.setTimeZone(record.getDeliveryTime()));
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    @Override
    public String toString() {
        return String.format("BasicEmailRecordDto{deliveryTime='%s', deliveryStatus='%s'}",
                deliveryTime, deliveryStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deliveryTime, deliveryStatus);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        BasicEmailRecordDto other = (BasicEmailRecordDto) obj;

        return Objects.equals(this.deliveryTime, other.deliveryTime) &&
                Objects.equals(this.deliveryStatus, other.deliveryStatus);
    }
}
