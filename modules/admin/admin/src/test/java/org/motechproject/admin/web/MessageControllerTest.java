package org.motechproject.admin.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.domain.NotificationRule;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.ActionType;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.admin.web.controller.MessageController;
import org.motechproject.admin.web.controller.NotificationRuleDto;
import org.motechproject.osgi.web.UIFrameworkService;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageControllerTest {

    @InjectMocks
    MessageController controller = new MessageController();

    @Mock
    StatusMessageService statusMessageService;

    @Mock
    List<StatusMessage> statusMessages;

    @Mock
    UIFrameworkService uiFrameworkService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetAllMessages() {
        when(statusMessageService.getAllMessages()).thenReturn(statusMessages);

        List<StatusMessage> result = controller.getMessages(true);

        assertEquals(statusMessages, result);
        verify(statusMessageService).getAllMessages();
        verify(uiFrameworkService).moduleBackToNormal("admin", "messages");
    }

    @Test
    public void testGetActiveMessagesNo() {
        when(statusMessageService.getActiveMessages()).thenReturn(statusMessages);

        List<StatusMessage> result = controller.getMessages(false);

        assertEquals(statusMessages, result);
        verify(statusMessageService).getActiveMessages();
    }

    @Test
    public void shouldReturnNotificationRulesList() {
        NotificationRule notificationRule = new NotificationRule();
        notificationRule.setActionType(ActionType.SMS);
        notificationRule.setRecipient("rec1");
        NotificationRule notificationRule2 = new NotificationRule();
        notificationRule2.setActionType(ActionType.EMAIL);
        notificationRule.setRecipient("rec2");
        when(statusMessageService.getNotificationRules()).thenReturn(asList(notificationRule, notificationRule2));

        List<NotificationRule> result =  controller.getNotificationRules();

        assertEquals(asList(notificationRule, notificationRule2), result);
        verify(statusMessageService).getNotificationRules();
    }

    @Test
    public void shouldRemoveNotificationRules() {
        controller.deleteRule("id");
        verify(statusMessageService).removeNotificationRule("id");
    }

    @Test
    public void shouldAddANotificationRule() {
        NotificationRule notificationRule = new NotificationRule();
        notificationRule.setActionType(ActionType.EMAIL);
        notificationRule.setRecipient("test");

        controller.addRule(notificationRule);

        verify(statusMessageService).saveRule(notificationRule);
    }

    @Test
    public void shouldHandleNotificationDto() {
        NotificationRule notificationRule1 = mock(NotificationRule.class);
        NotificationRule notificationRule2 = mock(NotificationRule.class);
        List<NotificationRule> ruleList = asList(notificationRule1, notificationRule2);

        NotificationRuleDto notificationRuleDto = new NotificationRuleDto();
        notificationRuleDto.setNotificationRules(ruleList);
        notificationRuleDto.setIdsToRemove(asList("id1", "id2"));

        controller.saveNotificationRules(notificationRuleDto);

        verify(statusMessageService).saveNotificationRules(ruleList);
        verify(statusMessageService).removeNotificationRule("id1");
        verify(statusMessageService).removeNotificationRule("id2");
    }
}
