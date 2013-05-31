package org.motechproject.admin.messages;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.domain.NotificationRule;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.notification.EmailNotifier;
import org.motechproject.admin.repository.AllNotificationRules;
import org.motechproject.admin.repository.AllStatusMessages;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.admin.service.impl.StatusMessageServiceImpl;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.osgi.web.UIFrameworkService;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class StatusMessageServiceTest {

    private static final String MODULE_NAME = "module";

    @InjectMocks
    private StatusMessageService statusMessageService = new StatusMessageServiceImpl();

    @Mock
    private AllStatusMessages allStatusMessages;

    @Mock
    private AllNotificationRules allNotificationRules;

    @Mock
    private StatusMessage mockMsg;

    @Mock
    private EmailSenderService emailSender;

    @Mock
    private EventRelay eventRelay;

    @Mock
    private UIFrameworkService uiFrameworkService;

    @Mock
    private EmailNotifier emailNotifier;

    StatusMessage activeMessage = new StatusMessage("active", MODULE_NAME, Level.INFO, DateTime.now().plusHours(1));

    StatusMessage inactiveMessage = new StatusMessage("inactive", MODULE_NAME, Level.INFO, DateTime.now().minusHours(1));
    List<StatusMessage> statusMessages = new ArrayList<>();

    @Before
    public void setUp() {
        initMocks(this);
        statusMessages.add(activeMessage);
        statusMessages.add(inactiveMessage);
    }

    @Test
    public void testGetAllMessages() {
        when(allStatusMessages.getAll()).thenReturn(statusMessages);

        List<StatusMessage> result = statusMessageService.getAllMessages();

        assertEquals(statusMessages, result);
        verify(allStatusMessages).getAll();
    }

    @Test
    public void testActiveMessages() {
        when(allStatusMessages.getActiveMessages()).thenReturn(asList(activeMessage));

        List<StatusMessage> result = statusMessageService.getActiveMessages();

        assertEquals(asList(activeMessage), result);
        verify(allStatusMessages).getActiveMessages();
    }

    @Test
    public void testPostMessage() {
        statusMessageService.postMessage(activeMessage);
        verify(allStatusMessages).add(activeMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPostMessageNullText() {
        StatusMessage illegalMessage = new StatusMessage(null, MODULE_NAME, Level.INFO);
        statusMessageService.postMessage(illegalMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPostMessageNullTimeout() {
        StatusMessage illegalMessage = new StatusMessage("text", MODULE_NAME, Level.INFO, null);
        statusMessageService.postMessage(illegalMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPostMessagePastTimeout() {
        statusMessageService.postMessage(inactiveMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPostMessageNullLevel() {
        StatusMessage illegal = new StatusMessage("text", MODULE_NAME, null);
        statusMessageService.postMessage(illegal);
    }

    @Test
    public void testRemoveMessage() {
        statusMessageService.removeMessage(mockMsg);
        verify(allStatusMessages).remove(mockMsg);
    }

    @Test
    public void shouldSaveNotificationRules() {
        NotificationRule notificationRule1 = new NotificationRule("rec1", ActionType.EMAIL);
        NotificationRule notificationRule2 = new NotificationRule("rec2", ActionType.SMS);
        NotificationRule notificationRule3 = new NotificationRule("rec3", ActionType.SMS);
        notificationRule2.setId("id");
        notificationRule3.setId("id");
        when(allNotificationRules.get("id")).thenReturn(notificationRule3);

        statusMessageService.saveNotificationRules(asList(notificationRule1, notificationRule2));

        ArgumentCaptor<NotificationRule> captor = ArgumentCaptor.forClass(NotificationRule.class);

        verify(allNotificationRules).add(captor.capture());
        assertNull(captor.getValue().getId());
        assertEquals("rec1", captor.getValue().getRecipient());
        assertEquals(ActionType.EMAIL, captor.getValue().getActionType());

        verify(allNotificationRules).get("id");
        verify(allNotificationRules).update(captor.capture());
        assertEquals("id", captor.getValue().getId());
        assertEquals("rec2", captor.getValue().getRecipient());
        assertEquals(ActionType.SMS, captor.getValue().getActionType());
    }

    @Test
    public void shouldReturnAllNotificationRules() {
        List<NotificationRule> expected = mock(List.class);
        when(allNotificationRules.getAll()).thenReturn(expected);

        assertEquals(expected, statusMessageService.getNotificationRules());
        verify(allNotificationRules).getAll();
    }

    @Test
    public void shouldRemoveANotificationRule() {
        NotificationRule notificationRule = mock(NotificationRule.class);
        when(allNotificationRules.get("id")).thenReturn(notificationRule);

        statusMessageService.removeNotificationRule("id");

        verify(allNotificationRules).remove(notificationRule);
    }

    @Test
    public void shouldSaveANewRule() {
        NotificationRule notificationRule = new NotificationRule("rec", ActionType.SMS);

        statusMessageService.saveRule(notificationRule);

        ArgumentCaptor<NotificationRule> captor = ArgumentCaptor.forClass(NotificationRule.class);
        verify(allNotificationRules).add(captor.capture());
        verify(allNotificationRules, never()).update(any(NotificationRule.class));

        assertNull(captor.getValue().getId());
        assertEquals("rec", captor.getValue().getRecipient());
        assertEquals(ActionType.SMS, captor.getValue().getActionType());
    }

    @Test
    public void shouldUpdateAnExistingRule() {
        NotificationRule notificationRule = new NotificationRule("rec", ActionType.SMS);
        NotificationRule existing = new NotificationRule("rec2", ActionType.EMAIL);
        notificationRule.setId("id");
        existing.setId("id");
        when(allNotificationRules.get("id")).thenReturn(existing);

        statusMessageService.saveRule(notificationRule);

        ArgumentCaptor<NotificationRule> captor = ArgumentCaptor.forClass(NotificationRule.class);
        verify(allNotificationRules).update(captor.capture());
        assertEquals("id", captor.getValue().getId());
        assertEquals("rec", captor.getValue().getRecipient());
        assertEquals(ActionType.SMS, captor.getValue().getActionType());
    }

    @Test
    public void shouldSendNotifications() {
        NotificationRule notificationRuleEmail1 = new NotificationRule("e@ma.il", ActionType.EMAIL);
        NotificationRule notificationRuleEmail2 = new NotificationRule("e2@ma.il", ActionType.EMAIL);
        NotificationRule notificationRuleSms1 = new NotificationRule("1111", ActionType.SMS);
        NotificationRule notificationRuleSms2 = new NotificationRule("2222", ActionType.SMS);

        when(allNotificationRules.getAll()).thenReturn(asList(notificationRuleEmail1, notificationRuleSms1, notificationRuleSms2,
                notificationRuleEmail2));

        StatusMessage statusMessage = new StatusMessage("text", "module", Level.CRITICAL);
        statusMessageService.postMessage(statusMessage);

        verify(allStatusMessages).add(statusMessage);
        verify(allNotificationRules).getAll();

        verify(emailNotifier).send(statusMessage,"e@ma.il");
        verify(emailNotifier).send(statusMessage,"e2@ma.il");

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captor.capture());

        assertEquals("SendSMS", captor.getValue().getSubject());
        assertEquals(asList("1111", "2222"), captor.getValue().getParameters().get("number"));
        assertEquals("Motech Critical: [module] text", captor.getValue().getParameters().get("message"));

        verify(uiFrameworkService).moduleNeedsAttention("admin", "messages", "");
        verify(uiFrameworkService).moduleNeedsAttention("module", "text");
    }
}
