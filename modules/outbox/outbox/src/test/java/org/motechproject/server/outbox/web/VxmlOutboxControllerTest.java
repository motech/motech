package org.motechproject.server.outbox.web;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.domain.VoiceMessageType;
import org.motechproject.outbox.api.service.VoiceOutboxService;
import org.motechproject.outbox.server.web.VxmlOutboxController;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class VxmlOutboxControllerTest {


    @InjectMocks
    VxmlOutboxController vxmlOutboxController = new VxmlOutboxController();

    @Mock
    private VoiceOutboxService voiceOutboxService;

    @Mock
    private HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    private PlatformSettingsService platformSettingsService;

    @Before
    public void initMocks() {

        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testNextOutboxMessage() {

        String voiceMessageTypeName = "voicemessagetypename";

        OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
        VoiceMessageType voiceMessageType = new VoiceMessageType();
        voiceMessageType.setVoiceMessageTypeName(voiceMessageTypeName);
        voiceMessage.setVoiceMessageType(voiceMessageType);

        String externalId = "ext1";
        when(request.getParameter("pId")).thenReturn(externalId);

        when(voiceOutboxService.getNextMessage(externalId, OutboundVoiceMessageStatus.PENDING)).thenReturn(voiceMessage);

        ModelAndView modelAndView = vxmlOutboxController.outboxMessage(request, response);

        Assert.assertEquals(voiceMessageTypeName, modelAndView.getViewName());


    }

    @Test
    public void testNextOutboxMessageException() {

        String externalId = "ext1";
        when(request.getParameter("pId")).thenReturn(externalId);
        when(voiceOutboxService.getNextMessage(externalId, OutboundVoiceMessageStatus.PENDING)).thenThrow(new RuntimeException());

        ModelAndView modelAndView = vxmlOutboxController.outboxMessage(request, response);

        Assert.assertEquals(VxmlOutboxController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());


    }

    @Test
    public void testNextOutboxMessageNoMessage() {


        ModelAndView modelAndView = vxmlOutboxController.outboxMessage(request, response);

        Assert.assertEquals(VxmlOutboxController.NO_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());


    }

    @Test
    public void testNextOutboxMessageInvalidMessageNoMessageType() {


        OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();

        String externalId = "ext1";
        when(request.getParameter("pId")).thenReturn(externalId);
        when(voiceOutboxService.getNextMessage(externalId, OutboundVoiceMessageStatus.PENDING)).thenReturn(voiceMessage);

        ModelAndView modelAndView = vxmlOutboxController.outboxMessage(request, response);

        Assert.assertEquals(VxmlOutboxController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());


    }

    @Test
    public void testOutboxMessage() {

        String messageId = "mID";
        String voiceMessageTypeName = "voicemessagetypename";

        OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
        VoiceMessageType voiceMessageType = new VoiceMessageType();
        voiceMessageType.setVoiceMessageTypeName(voiceMessageTypeName);
        voiceMessage.setVoiceMessageType(voiceMessageType);

        Mockito.when(request.getParameter("mId")).thenReturn(messageId);
        when(voiceOutboxService.getMessageById(messageId)).thenReturn(voiceMessage);

        ModelAndView modelAndView = vxmlOutboxController.outboxMessage(request, response);

        Assert.assertEquals(voiceMessageTypeName, modelAndView.getViewName());

    }

    @Test
    public void testOutboxMessageException() {

        String messageId = "mID";
        String voiceMessageTypeName = "voicemessagetypename";


        Mockito.when(request.getParameter("mId")).thenReturn(messageId);
        when(voiceOutboxService.getMessageById(messageId)).thenThrow(new RuntimeException());

        ModelAndView modelAndView = vxmlOutboxController.outboxMessage(request, response);

        Assert.assertEquals(VxmlOutboxController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void testSavedMessage() {

        String externalId = "1";
        String voiceMessageTypeName = "voicemessagetypename";

        OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
        VoiceMessageType voiceMessageType = new VoiceMessageType();
        voiceMessageType.setVoiceMessageTypeName(voiceMessageTypeName);
        voiceMessage.setVoiceMessageType(voiceMessageType);

        when(request.getParameter("pId")).thenReturn(externalId);
        when(voiceOutboxService.getNextMessage(externalId, OutboundVoiceMessageStatus.SAVED)).thenReturn(voiceMessage);

        ModelAndView modelAndView = vxmlOutboxController.savedMessage(request, response);

        Assert.assertEquals(voiceMessageTypeName, modelAndView.getViewName());

    }

    @Test
    public void testSavedMessageException() {


        when(voiceOutboxService.getNextMessage(anyString(), Matchers.<OutboundVoiceMessageStatus>any())).thenThrow(new RuntimeException());

        ModelAndView modelAndView = vxmlOutboxController.savedMessage(request, response);

        Assert.assertEquals(VxmlOutboxController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void testSavedMessageNoMessage() {

        String externalId = "1";
        when(voiceOutboxService.getNextMessage(externalId, OutboundVoiceMessageStatus.SAVED)).thenReturn(null);

        ModelAndView modelAndView = vxmlOutboxController.savedMessage(request, response);

        Assert.assertEquals(VxmlOutboxController.NO_SAVED_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void testSavedMessageInvalidNoMessageType() {

        String externalId = "1";

        OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();

        when(request.getParameter("pId")).thenReturn(externalId);
        when(voiceOutboxService.getNextMessage(externalId, OutboundVoiceMessageStatus.SAVED)).thenReturn(voiceMessage);

        ModelAndView modelAndView = vxmlOutboxController.savedMessage(request, response);

        Assert.assertEquals(VxmlOutboxController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());

    }


    @Test
    public void testMessageMenu() {

        String messageId = "mID";

        OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
        voiceMessage.setStatus(OutboundVoiceMessageStatus.PENDING);

        when(request.getParameter("mId")).thenReturn(messageId);
        when(voiceOutboxService.getMessageById(messageId)).thenReturn(voiceMessage);

        ModelAndView modelAndView = vxmlOutboxController.messageMenu(request, response);

        Assert.assertEquals(VxmlOutboxController.MESSAGE_MENU_TEMPLATE_NAME, modelAndView.getViewName());
        verify(voiceOutboxService).setMessageStatus(messageId, OutboundVoiceMessageStatus.PLAYED);
    }

    @Test
    public void testSavedMessageMenu() {

        String messageId = "mID";

        OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
        voiceMessage.setStatus(OutboundVoiceMessageStatus.SAVED);

        when(request.getParameter("mId")).thenReturn(messageId);
        when(voiceOutboxService.getMessageById(messageId)).thenReturn(voiceMessage);

        ModelAndView modelAndView = vxmlOutboxController.messageMenu(request, response);

        Assert.assertEquals(VxmlOutboxController.SAVED_MESSAGE_MENU_TEMPLATE_NAME, modelAndView.getViewName());
        verify(voiceOutboxService, times(0)).setMessageStatus(anyString(), Matchers.<OutboundVoiceMessageStatus>any());
    }

    @Test
    public void testMessageMenuNoMessageId() {

        String messageId = "mID";

        Mockito.when(request.getParameter("mId")).thenReturn(null);

        ModelAndView modelAndView = vxmlOutboxController.messageMenu(request, response);

        Assert.assertEquals(VxmlOutboxController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
    }

    @Test
    public void testMessageMenuException() {

        String messageId = "mID";

        Mockito.when(request.getParameter("mId")).thenReturn(messageId);
        when(voiceOutboxService.getMessageById(anyString())).thenThrow(new RuntimeException());

        ModelAndView modelAndView = vxmlOutboxController.messageMenu(request, response);

        Assert.assertEquals(VxmlOutboxController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
    }

    @Test
    public void testMessageMenuNoMessage() {

        String messageId = "mID";

        Mockito.when(request.getParameter("mId")).thenReturn(messageId);
        when(voiceOutboxService.getMessageById(messageId)).thenReturn(null);

        ModelAndView modelAndView = vxmlOutboxController.messageMenu(request, response);

        Assert.assertEquals(VxmlOutboxController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
    }


    @Test
    public void testSaveOutboxMessage() {

        String messageId = "mID";
        String voiceMessageTypeName = "voicemessagetypename";

        OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
        VoiceMessageType voiceMessageType = new VoiceMessageType();
        voiceMessageType.setVoiceMessageTypeName(voiceMessageTypeName);
        voiceMessage.setVoiceMessageType(voiceMessageType);

        Mockito.when(request.getParameter("mId")).thenReturn(messageId);
        when(voiceOutboxService.getMessageById(messageId)).thenReturn(new OutboundVoiceMessage());

        ModelAndView modelAndView = vxmlOutboxController.save(request, response);
        verify(voiceOutboxService).saveMessage(messageId);
        Assert.assertEquals(VxmlOutboxController.MESSAGE_SAVED_CONFIRMATION_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void testSaveOutboxMessageException() {

        String messageId = "mID";


        Mockito.when(request.getParameter(VxmlOutboxController.MESSAGE_ID_PARAM)).thenReturn(messageId);
        when(voiceOutboxService.getMessageById(messageId)).thenReturn(new OutboundVoiceMessage());
        doThrow(new RuntimeException()).when(voiceOutboxService).saveMessage(messageId);

        ModelAndView modelAndView = vxmlOutboxController.save(request, response);

        Assert.assertEquals(VxmlOutboxController.SAVE_MESSAGE_ERROR_TEMPLATE_NAME, modelAndView.getViewName());
        verify(voiceOutboxService).saveMessage(messageId);

    }

    @Test
    public void testSaveOutboxMessageNoMessageId() {

        Mockito.when(request.getParameter(VxmlOutboxController.MESSAGE_ID_PARAM)).thenReturn(null);

        ModelAndView modelAndView = vxmlOutboxController.save(request, response);

        Assert.assertEquals(VxmlOutboxController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
        verify(voiceOutboxService, times(0)).saveMessage(anyString());
    }

    @Test
    public void testRemoveOutboxMessage() {

        String messageId = "mID";


        when(request.getParameter("mId")).thenReturn(messageId);
        when(voiceOutboxService.getMessageById(messageId)).thenReturn(new OutboundVoiceMessage());

        ModelAndView modelAndView = vxmlOutboxController.remove(request, response);
        verify(voiceOutboxService).setMessageStatus(messageId, OutboundVoiceMessageStatus.PLAYED);
        Assert.assertEquals(VxmlOutboxController.MESSAGE_REMOVED_CONFIRMATION_TEMPLATE_NAME, modelAndView.getViewName());

    }


    @Test
    public void testRemoveOutboxMessageException() {

        String messageId = "mID";


        Mockito.when(request.getParameter(VxmlOutboxController.MESSAGE_ID_PARAM)).thenReturn(messageId);
        doThrow(new RuntimeException()).when(voiceOutboxService).setMessageStatus(anyString(), Matchers.<OutboundVoiceMessageStatus>any());

        ModelAndView modelAndView = vxmlOutboxController.remove(request, response);

        Assert.assertEquals(VxmlOutboxController.REMOVE_SAVED_MESSAGE_ERROR_TEMPLATE_NAME, modelAndView.getViewName());

    }

    @Test
    public void testRemoveOutboxMessageNoMessageId() {

        Mockito.when(request.getParameter(VxmlOutboxController.MESSAGE_ID_PARAM)).thenReturn(null);

        ModelAndView modelAndView = vxmlOutboxController.remove(request, response);

        Assert.assertEquals(VxmlOutboxController.ERROR_MESSAGE_TEMPLATE_NAME, modelAndView.getViewName());
        verify(voiceOutboxService, times(0)).setMessageStatus(anyString(), Matchers.<OutboundVoiceMessageStatus>any());
    }

}
