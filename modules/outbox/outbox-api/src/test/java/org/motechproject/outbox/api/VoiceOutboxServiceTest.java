package org.motechproject.outbox.api;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.outbox.api.contract.SortKey;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.repository.AllOutboundVoiceMessages;
import org.motechproject.outbox.api.service.impl.VoiceOutboxServiceImpl;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.EventRelay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class VoiceOutboxServiceTest {
    private static final int MAX_MESSAGES_PENDING = 15;

    @InjectMocks
    VoiceOutboxServiceImpl voiceOutboxService = new VoiceOutboxServiceImpl();
    @Mock
    AllOutboundVoiceMessages allOutboundVoiceMessages;
    @Mock
    EventRelay eventRelay;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        voiceOutboxService.setNumDaysKeepSavedMessages(10);
        voiceOutboxService.setMaxNumberOfPendingMessages(MAX_MESSAGES_PENDING);
    }

    @Test
    public void testAddMessage() {
        String externalId = "pid";

        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setExternalId(externalId);

        voiceOutboxService.addMessage(outboundVoiceMessage);

        when(allOutboundVoiceMessages.get(null)).thenThrow(new NullPointerException("Argument cannot be null"));
        verify(allOutboundVoiceMessages).add(outboundVoiceMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddMessageNullMessage() {

        voiceOutboxService.addMessage(null);

        verify(allOutboundVoiceMessages, never()).add(null);
    }

    @Test
    public void testGetNextMessageWithCreationTimeSortKey() {
        //given
        String externalId = "pid";

        OutboundVoiceMessage outboundVoiceMessage1 = new OutboundVoiceMessage();
        OutboundVoiceMessage outboundVoiceMessage2 = new OutboundVoiceMessage();

        List<OutboundVoiceMessage> pendingVoiceMessages = new ArrayList<OutboundVoiceMessage>();
        pendingVoiceMessages.add(outboundVoiceMessage1);
        pendingVoiceMessages.add(outboundVoiceMessage2);
        when(allOutboundVoiceMessages.getMessages(externalId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime)).thenReturn(pendingVoiceMessages);

        //when
        OutboundVoiceMessage nextMessage = voiceOutboxService.getNextMessage(externalId, OutboundVoiceMessageStatus.PENDING);

        //then
        verify(allOutboundVoiceMessages, times(1)).getMessages(externalId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);
        assertEquals(outboundVoiceMessage1, nextMessage);

    }

    @Test
    public void testGetNextMessageWithDSequenceNumberSortKey() {
        //given
        String externalId = "pid";

        OutboundVoiceMessage outboundVoiceMessage1 = new OutboundVoiceMessage();
        OutboundVoiceMessage outboundVoiceMessage2 = new OutboundVoiceMessage();

        List<OutboundVoiceMessage> pendingVoiceMessages = new ArrayList<OutboundVoiceMessage>();
        pendingVoiceMessages.add(outboundVoiceMessage1);
        pendingVoiceMessages.add(outboundVoiceMessage2);
        when(allOutboundVoiceMessages.getMessages(externalId, OutboundVoiceMessageStatus.PENDING, SortKey.SequenceNumber)).thenReturn(pendingVoiceMessages);

        //when
        OutboundVoiceMessage nextMessage = voiceOutboxService.getNextMessage(externalId, OutboundVoiceMessageStatus.PENDING, SortKey.SequenceNumber);

        //then
        verify(allOutboundVoiceMessages, times(1)).getMessages(externalId, OutboundVoiceMessageStatus.PENDING, SortKey.SequenceNumber);
        assertEquals(outboundVoiceMessage1, nextMessage);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextMessageNullExternalId() {

        voiceOutboxService.getNextMessage(null, OutboundVoiceMessageStatus.PENDING);

        verify(allOutboundVoiceMessages, never()).getMessages(null, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextMessageNullStatus() {

        voiceOutboxService.getNextMessage("extid", null);

        verify(allOutboundVoiceMessages, never()).getMessages("extid", null, SortKey.CreationTime);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextMessageNullSortKey() {

        voiceOutboxService.getNextMessage("extid", OutboundVoiceMessageStatus.PENDING, null);

        verify(allOutboundVoiceMessages, never()).getMessages(anyString(), Matchers.<OutboundVoiceMessageStatus>any(), Matchers.<SortKey>any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextMessageEmptyExternalId() {

        voiceOutboxService.getNextMessage("", OutboundVoiceMessageStatus.PENDING);

        verify(allOutboundVoiceMessages, never()).getMessages(anyString(), OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);

    }

    @Test
    public void testGetNextMessageNoMessages() {

        String externalId = "pid";

        when(allOutboundVoiceMessages.getMessages(externalId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime)).thenReturn(new ArrayList<OutboundVoiceMessage>());

        assertNull(voiceOutboxService.getNextMessage(externalId, OutboundVoiceMessageStatus.PENDING));
    }

    @Test
    public void testGetMessageById() {

        String messageId = "msgId";
        OutboundVoiceMessage message = new OutboundVoiceMessage();

        when(allOutboundVoiceMessages.get(messageId)).thenReturn(message);

        assertEquals(message, voiceOutboxService.getMessageById(messageId));

    }


    @Test(expected = IllegalArgumentException.class)
    public void testGetMessageByIdNullId() {

        voiceOutboxService.getMessageById(null);

        verify(allOutboundVoiceMessages, never()).get(Matchers.<String>anyObject());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testGetMessageByIdEmptyId() {

        voiceOutboxService.getMessageById("");

        verify(allOutboundVoiceMessages, never()).get(Matchers.<String>anyObject());
    }

    @Test
    public void testRemoveMessage() {

        String messageId = "msgId";
        OutboundVoiceMessage message = new OutboundVoiceMessage();

        when(allOutboundVoiceMessages.get(messageId)).thenReturn(message);

        voiceOutboxService.removeMessage(messageId);

        verify(allOutboundVoiceMessages, times(1)).safeRemove(message);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveMessageNullMessageId() {

        voiceOutboxService.removeMessage(null);

        verify(allOutboundVoiceMessages, never()).getMessages(anyString(), OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveMessageEmptyMessageId() {

        voiceOutboxService.removeMessage("");

        verify(allOutboundVoiceMessages, never()).getMessages(anyString(), OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);
    }

    @Test
    public void testRemoveMessageMessageNotExist() {
        //TODO - implement
    }

    @Test
    public void testSetMessageStatus() {

        String messageId = "msgId";

        when(allOutboundVoiceMessages.get(messageId)).thenReturn(new OutboundVoiceMessage());

        voiceOutboxService.setMessageStatus(messageId, OutboundVoiceMessageStatus.PLAYED);
        verify(allOutboundVoiceMessages).update(Matchers.<OutboundVoiceMessage>anyObject());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMessageStatusNullMessageId() {

        voiceOutboxService.setMessageStatus(null, OutboundVoiceMessageStatus.PLAYED);
        verify(allOutboundVoiceMessages, never()).update(Matchers.<OutboundVoiceMessage>anyObject());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMessageStatusEmptyMessageId() {

        voiceOutboxService.setMessageStatus("", OutboundVoiceMessageStatus.PLAYED);
        verify(allOutboundVoiceMessages, never()).update(Matchers.<OutboundVoiceMessage>anyObject());
    }

    @Test
    public void testGetNumberOfMessages() {

        String externalId = "pid";

        when(allOutboundVoiceMessages.getMessagesCount(externalId, OutboundVoiceMessageStatus.PENDING)).thenReturn(2);

        assertEquals(2, voiceOutboxService.getNumberOfMessages(externalId, OutboundVoiceMessageStatus.PENDING));
    }

    @Test
    public void testGetNumberPendingMessagesNoMessages() {

        String externalId = "pid";

        when(allOutboundVoiceMessages.getMessages(externalId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime)).thenReturn(new ArrayList<OutboundVoiceMessage>());

        assertEquals(0, voiceOutboxService.getNumberOfMessages(externalId, OutboundVoiceMessageStatus.PENDING));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumberPendingMessagesNullExternalId() {

        voiceOutboxService.getNumberOfMessages(null, OutboundVoiceMessageStatus.PENDING);

        verify(allOutboundVoiceMessages, never()).getMessages(null, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumberPendingMessagesNullMessageStatus() {

        String externalId = "external id";
        voiceOutboxService.getNumberOfMessages(externalId, null);

        verify(allOutboundVoiceMessages, never()).getMessages(anyString(), Matchers.<OutboundVoiceMessageStatus>any(), SortKey.CreationTime);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumberPendingMessagesEmptyExternalId() {

        voiceOutboxService.getNumberOfMessages("", OutboundVoiceMessageStatus.PENDING);

        verify(allOutboundVoiceMessages, never()).getMessages(anyString(), OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);

    }

    @Test(expected = IllegalArgumentException.class)
    public void externalIdShouldNotBeNull_getNumberOfMessages() {

        voiceOutboxService.getNumberOfMessages(null, OutboundVoiceMessageStatus.SAVED, "");
        verify(allOutboundVoiceMessages, never()).getMessagesCount(anyString(), OutboundVoiceMessageStatus.SAVED, anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void statusShouldNotBeNull_getNumberOfMessages() {

        voiceOutboxService.getNumberOfMessages("ext_id", null, "");
        verify(allOutboundVoiceMessages, never()).getMessagesCount(anyString(), Matchers.<OutboundVoiceMessageStatus>any(), anyString());
    }

    @Test
    public void getNumberOfMessages() {
        voiceOutboxService.getNumberOfMessages("ext_id", OutboundVoiceMessageStatus.SAVED, "message_type");
        verify(allOutboundVoiceMessages, times(1)).getMessagesCount("ext_id", OutboundVoiceMessageStatus.SAVED, "message_type");
    }

    @Test
    public void testSaveMessage() {

        String messageId = "msgId";

        OutboundVoiceMessage message = new OutboundVoiceMessage();

        when(allOutboundVoiceMessages.get(messageId)).thenReturn(message);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, voiceOutboxService.getNumDaysKeepSavedMessages());

        voiceOutboxService.saveMessage(messageId);
        verify(allOutboundVoiceMessages).update(message);
        assertEquals(OutboundVoiceMessageStatus.SAVED, message.getStatus());
        System.out.println(calendar.getTime().getTime() - message.getExpirationDate().getTime());
        assertTrue(message.getExpirationDate().getTime() - calendar.getTime().getTime() < 1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveMessageNoMessageId() {

        voiceOutboxService.saveMessage(null);
        verify(allOutboundVoiceMessages, never()).update(Matchers.<OutboundVoiceMessage>any());
    }

    @Test
    public void testMaxMessagesReached() {
        String externalId = "001";
        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setExternalId(externalId);

        when(allOutboundVoiceMessages.getMessagesCount(externalId, OutboundVoiceMessageStatus.PENDING)).thenReturn(MAX_MESSAGES_PENDING);
        voiceOutboxService.addMessage(outboundVoiceMessage);

        verify(allOutboundVoiceMessages).add(outboundVoiceMessage);

        ArgumentCaptor<MotechEvent> argument = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(argument.capture());
        assertEquals(argument.getValue().getSubject(), EventKeys.OUTBOX_MAX_PENDING_MESSAGES_EVENT_SUBJECT);
        assertEquals(EventKeys.getExternalID(argument.getValue()), externalId);
    }

    @Test
    public void testMaxPendingMessagesMoreAndLess() {
        String externalId = "001";
        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setExternalId(externalId);

        // LESS
        when(allOutboundVoiceMessages.getMessagesCount(externalId, OutboundVoiceMessageStatus.PENDING)).thenReturn(MAX_MESSAGES_PENDING - 1);
        voiceOutboxService.addMessage(outboundVoiceMessage);

        // MORE
        when(allOutboundVoiceMessages.getMessagesCount(externalId, OutboundVoiceMessageStatus.PENDING)).thenReturn(MAX_MESSAGES_PENDING + 1);
        voiceOutboxService.addMessage(outboundVoiceMessage);

        verify(allOutboundVoiceMessages, times(2)).add(outboundVoiceMessage);
        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
    }

    @Test
    public void getTheFirstMessage() {
        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        String externalId = "123";
        when(allOutboundVoiceMessages.getMessages(externalId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime)).thenReturn(Arrays.asList(outboundVoiceMessage));
        OutboundVoiceMessage nextMessage = voiceOutboxService.nextMessage(null, externalId);
        assertEquals(outboundVoiceMessage, nextMessage);
    }

    @Test
    public void markMessageAsRead() {
        String externalId = "123";
        OutboundVoiceMessage currentMessage = mock(OutboundVoiceMessage.class);
        String currentMessageId = "1";

        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setId("2");

        when(allOutboundVoiceMessages.get(currentMessageId)).thenReturn(currentMessage);
        when(allOutboundVoiceMessages.getMessages(externalId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime)).thenReturn(Arrays.asList(outboundVoiceMessage));

        OutboundVoiceMessage nextMessage = voiceOutboxService.nextMessage(currentMessageId, externalId);

        InOrder inOrder = inOrder(currentMessage, allOutboundVoiceMessages);
        assertEquals(outboundVoiceMessage, nextMessage);
        inOrder.verify(currentMessage).setStatus(OutboundVoiceMessageStatus.PLAYED);
        inOrder.verify(allOutboundVoiceMessages).update(currentMessage);
        inOrder.verify(allOutboundVoiceMessages).getMessages(externalId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);
    }

    @Test
    public void getAllMessagesByExternalIdAndStatusSortedBygivenSortKey() {
        String externalID = "ext_id";
        ArrayList<OutboundVoiceMessage> expectedResult = new ArrayList<OutboundVoiceMessage>();
        OutboundVoiceMessageStatus status = OutboundVoiceMessageStatus.PENDING;
        SortKey sequenceNumber = SortKey.SequenceNumber;
        when(allOutboundVoiceMessages.getMessages(externalID, status, sequenceNumber)).thenReturn(expectedResult);

        List<OutboundVoiceMessage> actualResult = voiceOutboxService.getMessages(externalID, status, sequenceNumber);

        assertEquals(expectedResult, actualResult);

        verify(allOutboundVoiceMessages, times(1)).getMessages(externalID, status, sequenceNumber);

    }
}