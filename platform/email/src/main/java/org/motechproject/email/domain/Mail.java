package org.motechproject.email.domain;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.motechproject.email.json.MailDeserializer;

import java.util.Objects;

/**
 * The <code>Mail</code> class represents an email message.
 */

@JsonDeserialize(using = MailDeserializer.class)
public class Mail {
    private String fromAddress;
    private String toAddress;
    private String subject;
    private String message;

    /**
     * Creates a new instance of <code>Mail</code>, with all fields set to
     * the values specified in the parameters.
     *
     * @param fromAddress  the email address of the sender
     * @param toAddress  the email address of the recipient
     * @param subject  the subject of the email
     * @param message  the body of the email
     */
    public Mail(String fromAddress, String toAddress, String subject, String message) {
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.subject = subject;
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
     * @deprecated As of release 0.21, replaced by {@link #getMessage()}
     */
    @Deprecated
    public String getText() {
        return message;
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
     * Returns a hash code value for this <code>Mail</code> object.
     *
     * @return a hash code value for this <code>Mail</code> object
     */
    @Override
    public int hashCode() {
        return Objects.hash(fromAddress, toAddress, subject, message);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * Returns true if this Mail and the object to compare have reference
     * equality or their field values are all equal.
     *
     * @param obj  The reference object with which to compare
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

        final Mail other = (Mail) obj;

        return Objects.equals(this.fromAddress, other.fromAddress)
                && Objects.equals(this.toAddress, other.toAddress)
                && Objects.equals(this.subject, other.subject)
                && Objects.equals(this.message, other.message);
    }

    /**
     * Returns a string representation of this <code>Mail</code> object.
     *
     * @return a string representation of this <code>Mail</code> object
     */
    @Override
    public String toString() {
        return String.format(
                "Mail{fromAddress='%s', toAddress='%s', subject='%s', message='%s'}",
                fromAddress, toAddress, subject, message);
    }
}
