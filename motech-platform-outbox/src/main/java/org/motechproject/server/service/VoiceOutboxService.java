package org.motechproject.server.service;

import org.motechproject.outbox.model.OutboundVoiceMessage;
import org.motechproject.outbox.model.OutboundVoiceMessageStatus;


/**
 *
 * @author Igor (iopushnyev@2paths.com)
 */
public interface VoiceOutboxService {

    public void addMessage(OutboundVoiceMessage outboundVoiceMessage);
    public OutboundVoiceMessage getNextPendingMessage(String patientId);
    public OutboundVoiceMessage getNextSavedMessage(String patientId);
    public void removeMessage(String outboundVoiceMessageId);
    public void saveMessage(String outboundVoiceMessageId);
    public void setMessageStatus(String outboundVoiceMessageId, OutboundVoiceMessageStatus status);
    public int getNumberMessages (String patientId);
    public int getNumberPendingMessages (String patientId);
    public int getNumberSavedMessages (String patientId);
}
