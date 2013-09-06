package org.motechproject.email.web;

import org.joda.time.format.DateTimeFormat;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.email.domain.EmailRecord;

import java.util.Objects;

/**
 * The <code>EmailRecordDto</code> class represents a single Email record, as it's seen by user
 * with full email log rights.
 */

public class EmailRecordDto extends BasicEmailRecordDto {

    private String fromAddress;
    private String toAddress;
    private String subject;
    private String message;

    public EmailRecordDto(EmailRecord record) {
        this.setDeliveryStatus(record.getDeliveryStatus().toString());
        this.setDeliveryTime(DateTimeFormat.forPattern("Y-MM-dd hh:mm:ss").print(DateUtil.setTimeZone(record.getDeliveryTime())));
        this.fromAddress = record.getFromAddress();
        this.toAddress = record.getToAddress();
        this.subject = record.getSubject();
        this.message = record.getMessage();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    @Override
    public String toString() {
        return String.format("EmailRecordDto{fromAddress='%s', toAddress='%s', subject='%s', message='%s', deliveryTime='%s', deliveryStatus='%s'}",
                fromAddress, toAddress, subject, message, this.getDeliveryTime(), this.getDeliveryStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromAddress, toAddress, subject, message, this.getDeliveryTime(), this.getDeliveryStatus());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        EmailRecordDto other = (EmailRecordDto) obj;

        return Objects.equals(this.toAddress, other.toAddress) &&
                Objects.equals(this.fromAddress, other.fromAddress) &&
                Objects.equals(this.subject, other.subject) &&
                Objects.equals(this.message, other.message) &&
                Objects.equals(this.getDeliveryTime(), other.getDeliveryTime()) &&
                Objects.equals(this.getDeliveryStatus(), other.getDeliveryStatus());
    }


}
