package org.motechproject.email.domain;

import org.motechproject.email.constants.EmailRolesConstants;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.util.SecurityMode;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * The <code>EmailRecord</code> class represents a record of a sent Email.
 * This class is exposed as an {@link org.motechproject.mds.annotations.Entity} through
 * Motech Data Services.
 *
 * @see org.motechproject.mds.annotations
 */
@Entity(nonEditable = true)
@Access(value = SecurityMode.PERMISSIONS, members = {EmailRolesConstants.BASIC_EMAIL_LOGS})
public class EmailRecord {

    private String fromAddress;

    @Field
    private Long id;

    @Field(required = true)
    private String toAddress;

    @Field
    private String subject;

    @Field(type = "text")
    private String message;

    @Field(required = true)
    private LocalDateTime deliveryTime;

    @Field(required = true)
    private DeliveryStatus deliveryStatus;

    /**
     * Creates a new instance of <code>EmailRecord</code>, with all fields set to null.
     */
    public EmailRecord() {
        this(null, null, null, null, null, null);
    }

    /**
     * Creates a new instance of <code>EmailRecord</code>, with all fields set to
     * the values specified in the parameters.
     *
     * @param fromAddress  the email address of the sender
     * @param toAddress  the email address of the recipient
     * @param subject  the subject of the email
     * @param message  the body of the email
     * @param deliveryTime  the date and time that the email was sent
     * @param deliveryStatus  the delivery status of the email
     */
    public EmailRecord(String fromAddress, String toAddress, String subject, String message, LocalDateTime deliveryTime, DeliveryStatus deliveryStatus) {
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.subject = subject;
        this.message = message;
        this.deliveryTime = deliveryTime;
        this.deliveryStatus = deliveryStatus;
    }

    /**
     * Sets the email address of the sender.
     *
     * @param fromAddress  the sender of the message
     */
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    /**
     * Sets the email address of the recipient.
     *
     * @param toAddress  the recipient of the message
     */
    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    /**
     * Sets the message subject.
     *
     * @param subject  the subject of the message
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Sets the message body.
     *
     * @param message  the body of the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the email address of the sender.
     *
     * @return the sender of the message
     */
    public String getFromAddress() {
        return fromAddress;
    }

    /**
     * Gets the email address of the recipient.
     *
     * @return the recipient of the message
     */
    public String getToAddress() {
        return toAddress;
    }

    /**
     * Gets the message subject.
     *
     * @return the subject of the message
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the message body.
     *
     * @return the body of the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the delivery time.
     *
     * @return the time that the email was sent
     */
    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    /**
     * Gets the delivery status.
     *
     * @return the delivery status of the message
     */
    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns a hash code value for this <code>EmailRecord</code> object.
     *
     * @return a hash code value for this <code>EmailRecord</code> object
     */
    @Override
    public int hashCode() {
        return Objects.hash(fromAddress, toAddress, subject, message, deliveryTime, deliveryStatus);
    }

    /**
     * Returns a string representation of this <code>EmailRecord</code> object.
     *
     * @return a string representation of this <code>EmailRecord</code> object
     */
    @Override
    public String toString() {
        return String.format("EmailRecord{fromAddress='%s', toAddress='%s', subject='%s', message='%s', deliveryTime='%s', deliveryStatus='%s'}",
                fromAddress, toAddress, subject, message, deliveryTime, deliveryStatus);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * Returns true if this EmailRecord and the object to compare have reference
     * equality or their field values are all equal.
     *
     * @param obj  The reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        EmailRecord other = (EmailRecord) obj;

        return Objects.equals(this.toAddress, other.toAddress) &&
               Objects.equals(this.fromAddress, other.fromAddress) &&
               Objects.equals(this.subject, other.subject) &&
               Objects.equals(this.message, other.message) &&
               Objects.equals(this.deliveryTime, other.deliveryTime) &&
               Objects.equals(this.deliveryStatus, other.deliveryStatus);
    }

}
