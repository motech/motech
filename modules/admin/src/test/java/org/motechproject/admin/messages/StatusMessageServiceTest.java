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
import org.motechproject.admin.service.NotificationRulesDataService;
import org.motechproject.admin.service.StatusMessagesDataService;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.admin.service.impl.StatusMessageServiceImpl;
import org.motechproject.commons.api.Range;
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
    private StatusMessagesDataService statusMessagesDataService;

    @Mock
    private NotificationRulesDataService notificationRulesDataService;

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
        when(statusMessagesDataService.retrieveAll()).thenReturn(statusMessages);

        List<StatusMessage> result = statusMessageService.getAllMessages();

        assertEquals(statusMessages, result);
        verify(statusMessagesDataService).retrieveAll();
    }

    @Test
    public void testActiveMessages() {
        when(statusMessagesDataService.findByTimeout(any(Range.class))).thenReturn(asList(activeMessage));

        List<StatusMessage> result = statusMessageService.getActiveMessages();

        assertEquals(asList(activeMessage), result);
        verify(statusMessagesDataService).findByTimeout(any(Range.class));
    }

    @Test
    public void testPostMessage() {
        statusMessageService.postMessage(activeMessage);
        verify(statusMessagesDataService).create(activeMessage);
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
        verify(statusMessagesDataService).delete(mockMsg);
    }

    @Test
    public void shouldSaveNotificationRules() {
        NotificationRule notificationRule1 = new NotificationRule("rec1", ActionType.EMAIL);
        NotificationRule notificationRule2 = new NotificationRule("rec2", ActionType.SMS);
        NotificationRule notificationRule3 = new NotificationRule("rec3", ActionType.SMS);
        notificationRule2.setId(1L);
        notificationRule3.setId(1L);
        when(notificationRulesDataService.findById(1L)).thenReturn(notificationRule3);

        statusMessageService.saveNotificationRules(asList(notificationRule1, notificationRule2));

        ArgumentCaptor<NotificationRule> captor = ArgumentCaptor.forClass(NotificationRule.class);

        verify(notificationRulesDataService).create(captor.capture());
        assertNull(captor.getValue().getId());
        assertEquals("rec1", captor.getValue().getRecipient());
        assertEquals(ActionType.EMAIL, captor.getValue().getActionType());

        verify(notificationRulesDataService).findById(1L);
        verify(notificationRulesDataService).update(captor.capture());
        assertEquals(1L, captor.getValue().getId().longValue());
        assertEquals("rec2", captor.getValue().getRecipient());
        assertEquals(ActionType.SMS, captor.getValue().getActionType());
    }

    @Test
    public void shouldReturnAllNotificationRules() {
        List<NotificationRule> expected = mock(List.class);
        when(notificationRulesDataService.retrieveAll()).thenReturn(expected);

        assertEquals(expected, statusMessageService.getNotificationRules());
        verify(notificationRulesDataService).retrieveAll();
    }

    @Test
    public void shouldRemoveANotificationRule() {
        NotificationRule notificationRule = mock(NotificationRule.class);
        when(notificationRulesDataService.findById(1L)).thenReturn(notificationRule);

        statusMessageService.removeNotificationRule(1L);

        verify(notificationRulesDataService).delete(notificationRule);
    }

    @Test
    public void shouldSaveANewRule() {
        NotificationRule notificationRule = new NotificationRule("rec", ActionType.SMS);

        statusMessageService.saveRule(notificationRule);

        ArgumentCaptor<NotificationRule> captor = ArgumentCaptor.forClass(NotificationRule.class);
        verify(notificationRulesDataService).create(captor.capture());
        verify(notificationRulesDataService, never()).update(any(NotificationRule.class));

        assertNull(captor.getValue().getId());
        assertEquals("rec", captor.getValue().getRecipient());
        assertEquals(ActionType.SMS, captor.getValue().getActionType());
    }

    @Test
    public void shouldUpdateAnExistingRule() {
        NotificationRule notificationRule = new NotificationRule("rec", ActionType.SMS);
        NotificationRule existing = new NotificationRule("rec2", ActionType.EMAIL);
        notificationRule.setId(1L);
        existing.setId(1L);
        when(notificationRulesDataService.findById(1L)).thenReturn(existing);

        statusMessageService.saveRule(notificationRule);

        ArgumentCaptor<NotificationRule> captor = ArgumentCaptor.forClass(NotificationRule.class);
        verify(notificationRulesDataService).update(captor.capture());
        assertEquals(1L, captor.getValue().getId().longValue());
        assertEquals("rec", captor.getValue().getRecipient());
        assertEquals(ActionType.SMS, captor.getValue().getActionType());
    }

    @Test
    public void shouldSendNotifications() {
        NotificationRule notificationRuleEmail1 = new NotificationRule("e@ma.il", ActionType.EMAIL);
        NotificationRule notificationRuleEmail2 = new NotificationRule("e2@ma.il", ActionType.EMAIL);
        NotificationRule notificationRuleSms1 = new NotificationRule("1111", ActionType.SMS);
        NotificationRule notificationRuleSms2 = new NotificationRule("2222", ActionType.SMS);

        when(notificationRulesDataService.retrieveAll()).thenReturn(asList(notificationRuleEmail1, notificationRuleSms1, notificationRuleSms2,
                notificationRuleEmail2));

        StatusMessage statusMessage = new StatusMessage("text", "module", Level.CRITICAL);
        statusMessageService.postMessage(statusMessage);

        verify(statusMessagesDataService).create(statusMessage);
        verify(notificationRulesDataService).retrieveAll();

        verify(emailNotifier).send(statusMessage, "e@ma.il");
        verify(emailNotifier).send(statusMessage, "e2@ma.il");

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captor.capture());

        assertEquals("send_sms", captor.getValue().getSubject());
        assertEquals(asList("1111", "2222"), captor.getValue().getParameters().get("recipients"));
        assertEquals("Motech Critical: [module] text", captor.getValue().getParameters().get("message"));

        verify(uiFrameworkService).moduleNeedsAttention("admin", "messages", "");
        verify(uiFrameworkService).moduleNeedsAttention("module", "text");
    }
}
