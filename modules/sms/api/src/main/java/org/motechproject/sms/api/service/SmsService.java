package org.motechproject.sms.api.service;

/**
 * \ingroup sms
 * SmsService defines an interface for sending sms
 */
public interface SmsService {
    /**
     * Raises a SendSMS event for sending an sms to one or more recipients. An accompanying listener must be implemented which can deliver the message by interfacing with an sms provider.
     * @param request - a send sms request with mandatory list of recipients and a message
     * The payload raised with the event has the following values:
     * number - List<String> containing the recipients for the message
     * message - String containing the message text
     * delivery_time - org.joda.DateTime used to schedule the delivery of the message at the specified time
     * @since 0.18
     * This replaces existing sendSms() calls with flattened parameters. The parameters are wrapped within {@link SendSmsRequest} now.
     */
    void sendSMS(SendSmsRequest request);
}
