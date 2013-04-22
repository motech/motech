package org.motechproject.admin.email;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.Level;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.MotechSettings;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.Writer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailSenderTest {

    @InjectMocks
    private EmailSender emailSender = new EmailSenderImpl();

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private VelocityEngine velocityEngine;

    @Mock
    private PlatformSettingsService settingsService;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MotechSettings motechSettings;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldSendCriticalNotification() throws Exception {
        StatusMessage statusMessage = new StatusMessage("test msg", "test-module", Level.CRITICAL);
        when(settingsService.getPlatformSettings()).thenReturn(motechSettings);
        when(motechSettings.getServerUrl()).thenReturn("test.com");

        emailSender.sendCriticalNotificationEmail("test@address.com", statusMessage);

        ArgumentCaptor<MimeMessagePreparator> preparatorCaptor = ArgumentCaptor.forClass(MimeMessagePreparator.class);
        verify(javaMailSender).send(preparatorCaptor.capture());
        preparatorCaptor.getValue().prepare(mimeMessage);

        verify(mimeMessage).setRecipient(Message.RecipientType.TO, new InternetAddress("test@address.com"));
        verify(mimeMessage).setFrom(new InternetAddress("noreply@test.com"));
        verify(mimeMessage).setSubject("Critical notification raised in Motech");

        ArgumentCaptor<VelocityContext> velocityContextCaptor = ArgumentCaptor.forClass(VelocityContext.class);
        verify(velocityEngine).mergeTemplate(eq("/mail/criticalNotification.vm"), velocityContextCaptor.capture(), any(Writer.class));
        VelocityContext vc = velocityContextCaptor.getValue();

        assertEquals("test msg", vc.get("msg"));
        assertEquals("test-module", vc.get("module"));
        assertEquals("http://test.com/module/server/?moduleName=admin#/messages", vc.get("msgLink"));
    }
}
