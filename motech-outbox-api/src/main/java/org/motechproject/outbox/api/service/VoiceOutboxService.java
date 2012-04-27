package org.motechproject.outbox.api.service;

import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;

/**
 * \defgroup Outbox
 */
/**
 * \ingroup Outbox
 * Voice Outbox Service interface
 * Provides methods to get information and manage messages in the party voice outbox.
 * Each party (Patient, Nurse, Doctor, etc.) can have a voice outbox.
 *
 * @author Igor (iopushnyev@2paths.com)
 */
public interface VoiceOutboxService {

    /**
     * Adds the given outbound voice message to the parties' voice outbox.A party determines by external ID
     * in the given outbound voice message
     *
     * @param outboundVoiceMessage - outbound voice message to be added to the outbox
     */
    public void addMessage(OutboundVoiceMessage outboundVoiceMessage);

    /**
     * Returns the next pending message from the outbox of the party identified by the given external ID, or NULL
     * if there is no more pending messages in the outbox.
     * A next pending message is a not expired message with the status "Pending" that belongs
     * to this party, the oldest creation time and the highest priority among other pending messages in the external outbox.
     *
     * @param externalId - unique identifier of the party from
     * @return OutboundVoiceMessage
     */
    public OutboundVoiceMessage getNextPendingMessage(String externalId);

    /**
     * Returns the next saved message from the outbox of the party identified by the given external ID, or NULL
     * if there is no more saved messages in the outbox.
     *
     * @param externalId - unique identifier of the party from
     * @return OutboundVoiceMessage
     */
    public OutboundVoiceMessage getNextSavedMessage(String externalId);

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
     * Saves the message with given id in the outbox for period (number of days) specified in the outbox configuration
     * Sets to the message status: SAVED and expiration date: current date + number of days in the outbox configuration
     *
     * @param outboundVoiceMessageId
     */
    public void saveMessage(String outboundVoiceMessageId);

    /**
     * Returns number of pending messages in the voice outbox of the party with the given external ID
     *
     * @param externalId
     * @return
     */
    public int getNumberPendingMessages(String externalId);

    /**
     * Returns number of pending messages in the voice outbox of the party with the given external ID and the voice message type name
     *
     * @param externalId
     * @param voiceMessageTypeName
     * @return
     */
    public int getNumberPendingMessages(String externalId, String voiceMessageTypeName);

    /**
     * Sets the number of days for which a messages saved by the patient will be kept in outbox as SAVED messages
     *
     * @param numDayskeepSavedMessages
     */
    public void setNumDayskeepSavedMessages(int numDayskeepSavedMessages);

    /**
     * Returns number of days for which a messages saved by the patient will be kept in outbox as SAVED messages
     *
     * @return
     */
    public int getNumDayskeepSavedMessages();

    /**
     * Sets max number of pending messages after which the outbox will send an event
     *
     * @param maxNumberOfPendingMessages
     */
    public void setMaxNumberOfPendingMessages(int maxNumberOfPendingMessages);

    /**
     * @return max number of pending messages
     */
    public int getMaxNumberOfPendingMessages();

    public OutboundVoiceMessage nextMessage(String lastMessageId, String externalId);
}
