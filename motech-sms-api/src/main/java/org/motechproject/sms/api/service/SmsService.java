package org.motechproject.sms.api.service;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
/**
 * \ingroup sms
 * SmsService defines an interface for sending sms
 */
public interface SmsService {
    /**
     * Raises a SendSMS event for sending an sms to a recipient. An accompanying listener must be implemented which can deliver the message by interfacing with an sms provider.
     * @param recipient - the phone number of the recipient
     * @param message - the message text
     * The payload raised with the event has the following values:
     * number - List<String> containing the recipients for the message
     * message - String containing the message text
     * delivery_time - org.joda.DateTime used to schedule the delivery of the message at the specified time
     */
    void sendSMS(String recipient, String message);
    /**
     * Raises a SendSMS event for sending an sms to multiple recipients. An accompanying listener must be implemented which can deliver the message by interfacing with an sms provider.
     * @param recipients - list of phone numbers specifying the recipients
     * @param message - the message text
     * The payload raised with the event has the following values:
     * number - List<String> containing the recipients for the message
     * message - String containing the message text
     * delivery_time - org.joda.DateTime used to schedule the delivery of the message at the specified time
     */
    void sendSMS(List<String> recipients, String message);
    /**
     * Raises a SendSMS event for scheduled delivery of an sms to a recipient. An accompanying listener must be implemented which can deliver the message by interfacing with an sms provider.
     * @param recipient - the phone number of the recipient
     * @param message - the message text
     * @param deliveryTime - the time at which the sms must be delivered
     * The payload raised with the event has the following values:
     * number - List<String> containing the recipients for the message
     * message - String containing the message text
     * delivery_time - org.joda.DateTime used to schedule the delivery of the message at the specified time
     */
    void sendSMS(String recipient, String message, DateTime deliveryTime);
    /**
     * Raises a SendSMS event for scheduled delivery of an sms to mulitple recipients. An accompanying listener must be implemented which can deliver the message by interfacing with an sms provider.
     * @param recipients - list of phone numbers specifying the recipients
     * @param message - the message text
     * @param deliveryTime - the time at which the sms must be delivered
     * The payload raised with the event has the following values:
     * number - List<String> containing the recipients for the message
     * message - String containing the message text
     * delivery_time - org.joda.DateTime used to schedule the delivery of the message at the specified time
     */
    void sendSMS(ArrayList<String> recipients, String message, DateTime deliveryTime);
}
