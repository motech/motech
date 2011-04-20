package org.motechproject.server.service;

import org.motechproject.outbox.model.OutboundVoiceMessage;
import org.motechproject.outbox.model.OutboundVoiceMessageStatus;


/**
 * Voice Outbox Service interface
 * Provides methods to get information and manage messages in the party voice outbox.
 * Each party (Patient, Nurse, Doctor, etc.) can have a voice outbox.
 *
 *
 * @author Igor (iopushnyev@2paths.com)
 */
public interface VoiceOutboxService {

    /**
     * Adds the given outbound voice message to the parties' voice outbox.A party determines by party ID
     * in the given outbound voice message
     *
     * @param outboundVoiceMessage - outbound voice message to be added to the outbox
     */
    public void addMessage(OutboundVoiceMessage outboundVoiceMessage);

    /**
     * Returns the next pending message from the outbox of the party identified by the given party ID.
     * A next pending message in a party outbox considered a not expired message with status "Pending" that belongs
     * to this party, has the oldest creation time and  highest priority.
     *
     * @param partyId - unique identifier of the party from
     * @return OutboundVoiceMessage
     */
    public OutboundVoiceMessage getNextPendingMessage(String partyId);

    /**
     * Returns the outbound voice messages with the given message ID stored in the outbox
     *
     * @param outboundVoiceMessageId
     * @return OutboundVoiceMessage
     */
    public OutboundVoiceMessage getMessageById(String outboundVoiceMessageId);

     /**
     * Removes the messages with the given message ID from the outbox
     *
     * @param outboundVoiceMessageId
     */
    public void removeMessage(String outboundVoiceMessageId);

    /**
     * Sets the given status to the message with the given message ID in the outbox
     *
     * @param outboundVoiceMessageId
     * @param status
     */
    public void setMessageStatus(String outboundVoiceMessageId, OutboundVoiceMessageStatus status);

    /**
     * Returns number of pending messages in the voice outbox of the party with the given party ID
     *
     * @param partyId
     * @return
     */
    public int getNumberPendingMessages (String partyId);
}
