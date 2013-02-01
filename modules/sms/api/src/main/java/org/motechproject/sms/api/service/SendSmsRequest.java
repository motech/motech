package org.motechproject.sms.api.service;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class SendSmsRequest {

    @NotNull
    @Size(min = 1)
    @JsonProperty
    private List<String> recipients;
    @NotNull
    @Size(min = 1)
    @JsonProperty
    private String message;
    @JsonProperty
    private DateTime deliveryTime;

    /**
     * Request to raise a SendSMS event for scheduled delivery of an sms to multiple recipients. An accompanying listener must be implemented which can deliver the message by interfacing with an sms provider.
     * @param recipients - list of phone numbers specifying the recipients
     * @param message - the message text
     * @param deliveryTime - the time at which the sms must be delivered
     * The payload raised with the event has the following values:
     * number - List<String> containing the recipients for the message
     * message - String containing the message text
     * delivery_time - org.joda.DateTime used to schedule the delivery of the message at the specified time
     */
    public SendSmsRequest(List<String> recipients, String message, DateTime deliveryTime) {
        this.recipients = recipients;
        this.message = message;
        this.deliveryTime = deliveryTime;
    }

    /**
     * Request to raise a SendSMS event for delivery of an sms to mulitple recipients. An accompanying listener must be implemented which can deliver the message by interfacing with an sms provider.
     * @param recipients - list of phone numbers specifying the recipients
     * @param message - the message text
     * The payload raised with the event has the following values:
     * number - List<String> containing the recipients for the message
     * message - String containing the message text
     */
    public SendSmsRequest(List<String> recipients, String message) {
        this(recipients, message, null);
    }

    private SendSmsRequest() {
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public String getMessage() {
        return message;
    }

    public DateTime getDeliveryTime() {
        return deliveryTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SendSmsRequest)) {
            return false;
        }

        SendSmsRequest that = (SendSmsRequest) o;

        if (deliveryTime != null ? !deliveryTime.equals(that.deliveryTime) : that.deliveryTime != null) {
            return false;
        }
        if (message != null ? !message.equals(that.message) : that.message != null) {
            return false;
        }
        if (recipients != null ? !recipients.equals(that.recipients) : that.recipients != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = recipients != null ? recipients.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (deliveryTime != null ? deliveryTime.hashCode() : 0);
        return result;
    }
}
