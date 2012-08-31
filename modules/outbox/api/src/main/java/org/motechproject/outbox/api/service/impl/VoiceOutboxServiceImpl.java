package org.motechproject.outbox.api.service.impl;


import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.MotechObject;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.outbox.api.EventKeys;
import org.motechproject.outbox.api.contract.SortKey;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.repository.AllOutboundVoiceMessages;
import org.motechproject.outbox.api.service.VoiceOutboxService;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.List;

public class VoiceOutboxServiceImpl extends MotechObject implements VoiceOutboxService {

    private final Logger log = LoggerFactory.getLogger(VoiceOutboxServiceImpl.class);
    private int numDaysKeepSavedMessages;

    private int maxNumberOfPendingMessages = Integer.MAX_VALUE;

    @Autowired
    private EventRelay eventRelay;

    @Autowired
    private AllOutboundVoiceMessages allOutboundVoiceMessages;

    @Override
    @SuppressWarnings("unchecked")
    public void addMessage(OutboundVoiceMessage outboundVoiceMessage) {
        assertArgumentNotNull("OutboundVoiceMessage", outboundVoiceMessage);
        logInfo("Add message: %s", outboundVoiceMessage);

        outboundVoiceMessage.setStatus(OutboundVoiceMessageStatus.PENDING);
        outboundVoiceMessage.setCreationTime(DateUtil.now().toDate());
        allOutboundVoiceMessages.add(outboundVoiceMessage);

        //sends max-pending-messages event if needed
        String externalId = outboundVoiceMessage.getExternalId();
        Assert.hasText(externalId, "VoiceMessage must have a valid externalId");
        int msgNum = allOutboundVoiceMessages.getMessagesCount(externalId, OutboundVoiceMessageStatus.PENDING);
        if (maxNumberOfPendingMessages == msgNum) {
            log.warn(String.format("Max number (%d) of pending messages reached!", msgNum));
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.OUTBOX_MAX_PENDING_MESSAGES_EVENT_SUBJECT, ArrayUtils.toMap(new Object[][]{{EventKeys.EXTERNAL_ID_KEY, externalId}})));
        }
    }

    @Override
    public OutboundVoiceMessage getNextMessage(String externalId, OutboundVoiceMessageStatus messageStatus) {
        return getNextMessage(externalId, messageStatus, SortKey.CreationTime);
    }

    public OutboundVoiceMessage getNextMessage(String externalId, OutboundVoiceMessageStatus messageStatus, SortKey sortKey) {
        assertArgumentNotEmpty("ExternalId", externalId);
        assertArgumentNotNull("OutboundVoiceMessageStatus", messageStatus);
        assertArgumentNotNull("SortKey", sortKey);
        logInfo("Get next message for the external ID: %s with status %s", externalId, messageStatus);

        List<OutboundVoiceMessage> voiceMessages = allOutboundVoiceMessages.getMessages(externalId, messageStatus, sortKey);
        return voiceMessages.size() > 0 ? voiceMessages.get(0) : null;
    }

    @Override
    public OutboundVoiceMessage getMessageById(String outboundVoiceMessageId) {
        assertArgumentNotEmpty("OutboundVoiceMessageId", outboundVoiceMessageId);
        logInfo("Get message by ID: %s", outboundVoiceMessageId);
        return allOutboundVoiceMessages.get(outboundVoiceMessageId);
    }

    @Override
    public void removeMessage(String outboundVoiceMessageId) {
        assertArgumentNotEmpty("OutboundVoiceMessageId", outboundVoiceMessageId);
        logInfo("Remove message ID: %s", outboundVoiceMessageId);
        OutboundVoiceMessage outboundVoiceMessage = getMessageById(outboundVoiceMessageId);
        allOutboundVoiceMessages.safeRemove(outboundVoiceMessage);
    }

    @Override
    public void setMessageStatus(String outboundVoiceMessageId, OutboundVoiceMessageStatus status) {
        assertArgumentNotEmpty("OutboundVoiceMessageId", outboundVoiceMessageId);
        logInfo("Set status: %s to the message ID: %s", status, outboundVoiceMessageId);
        OutboundVoiceMessage outboundVoiceMessage = allOutboundVoiceMessages.get(outboundVoiceMessageId);
        outboundVoiceMessage.setStatus(status);
        allOutboundVoiceMessages.update(outboundVoiceMessage);
    }

    @Override
    public void saveMessage(String outboundVoiceMessageId) {
        assertArgumentNotEmpty("OutboundVoiceMessageId", outboundVoiceMessageId);
        logInfo("Save in the outbox message ID: %s", outboundVoiceMessageId);

        OutboundVoiceMessage outboundVoiceMessage = getMessageById(outboundVoiceMessageId);
        outboundVoiceMessage.setStatus(OutboundVoiceMessageStatus.SAVED);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, numDaysKeepSavedMessages);
        outboundVoiceMessage.setExpirationDate(calendar.getTime());
        allOutboundVoiceMessages.update(outboundVoiceMessage);
    }

    @Override
    public int getNumberOfMessages(String externalId, OutboundVoiceMessageStatus messageStatus) {
        logInfo("Get number of messages for the external ID: %s", externalId);
        assertArgumentNotEmpty("ExternalID", externalId);
        assertArgumentNotNull("OutboundVoiceMessageStatus", messageStatus);
        return allOutboundVoiceMessages.getMessagesCount(externalId, messageStatus);
    }

    @Override
    public int getNumberOfMessages(String externalId, OutboundVoiceMessageStatus messageStatus, String voiceMessageTypeName) {
        logInfo("Get number of pending messages for the external ID: %s", externalId);
        assertArgumentNotEmpty("ExternalID", externalId);
        assertArgumentNotNull("OutboundVoiceMessageStatus", messageStatus);
        return allOutboundVoiceMessages.getMessagesCount(externalId, messageStatus, voiceMessageTypeName);
    }

    @Override
    public List<OutboundVoiceMessage> getMessages(String externalId, OutboundVoiceMessageStatus status, SortKey sortKey) {
        return allOutboundVoiceMessages.getMessages(externalId, status, sortKey);
    }

    @Override
    public int getNumDaysKeepSavedMessages() {
        return numDaysKeepSavedMessages;
    }

    @Override
    public void setNumDaysKeepSavedMessages(int numDaysKeepSavedMessages) {
        this.numDaysKeepSavedMessages = numDaysKeepSavedMessages;
    }

    @Override
    public void setMaxNumberOfPendingMessages(int maxNumberOfPendingMessages) {
        this.maxNumberOfPendingMessages = maxNumberOfPendingMessages;
    }

    @Override
    public int getMaxNumberOfPendingMessages() {
        return this.maxNumberOfPendingMessages;
    }

    @Override
    public OutboundVoiceMessage nextMessage(String lastMessageId, String externalId) {
        if (StringUtils.isNotEmpty(lastMessageId)) {
            setMessageStatus(lastMessageId, OutboundVoiceMessageStatus.PLAYED);
        }
        return getNextMessage(externalId, OutboundVoiceMessageStatus.PENDING);
    }
}
