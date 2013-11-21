package org.motechproject.admin.notification;

import org.apache.velocity.app.VelocityEngine;
import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.Level;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.server.config.domain.MotechSettings;

import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class EmailNotifierTest {
    @Mock
    private SettingsFacade settingsFacade;

    @Mock
    private VelocityEngine velocityEngine;

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    EmailNotifier emailNotifier;
    private MotechSettings motechSettings;

    @Before
    public void setUp() {
        emailNotifier = new EmailNotifier();
        MockitoAnnotations.initMocks(this);

        motechSettings = mock(MotechSettings.class);
        when(settingsFacade.getPlatformSettings()).thenReturn(motechSettings);
    }

    @Test
    public void shouldSendNotification() throws Exception {
        String text = "Mail msg";
        DateTime datetime = new DateTime(2010, 12, 20, 10, 50);
        String moduleName = "admin";
        StatusMessage statusMessage = statusMessage(text, datetime, moduleName);

        when(motechSettings.getServerUrl()).thenReturn("http://serverurl");
        when(motechSettings.getServerHost()).thenReturn("serverurl");

        EmailNotifier emailNotifierSpy = spy(emailNotifier);

        ArgumentCaptor<Map> velocityArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        doReturn(text).when(emailNotifierSpy).mergeTemplateIntoString(velocityArgumentCaptor.capture());

        emailNotifierSpy.send(statusMessage, "recipients");

        Assert.assertEquals(text, velocityArgumentCaptor.getValue().get("msg"));

        String msgLink = "http://serverurl/module/server/?moduleName=admin#/messages";
        Assert.assertEquals(msgLink, velocityArgumentCaptor.getValue().get("msgLink"));
        Assert.assertTrue(velocityArgumentCaptor.getValue().get("dateTime").toString().matches("^(12[./]20|20[./]12)[./]10 10:50(| AM)$"));
        Assert.assertEquals(moduleName, velocityArgumentCaptor.getValue().get("module"));

        ArgumentCaptor<Mail> argument = ArgumentCaptor.forClass(Mail.class);
        Mockito.verify(emailSenderService).send(argument.capture());
        Mail mail = argument.getValue();

        Assert.assertEquals(text, mail.getMessage());
        Assert.assertEquals("recipients", mail.getToAddress());
        Assert.assertEquals("noreply@serverurl",mail.getFromAddress());
    }



    @Test
    public void shouldNotAddSchemeWhenHttpIsPartOfTheUrl() {
        when(motechSettings.getServerUrl()).thenReturn("https://serverurl");
        assertThat(emailNotifier.messagesUrl(), IsEqual.equalTo("https://serverurl/module/server/?moduleName=admin#/messages"));
    }

    @Test
    public void shouldJustReturnUrlPathWhenServerUrlIsNotGiven() {
        when(motechSettings.getServerUrl()).thenReturn(null);
        assertThat(emailNotifier.messagesUrl(), IsEqual.equalTo("/module/server/?moduleName=admin#/messages"));
    }

    private StatusMessage statusMessage(String text, DateTime datetime, String moduleName) {
        StatusMessage statusMessage = new StatusMessage(text, moduleName, Level.WARN);
        statusMessage.setDate(datetime);
        return statusMessage;
    }

}
