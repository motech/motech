package org.motechproject.email.osgi;

import org.motechproject.commons.api.MotechException;
import org.motechproject.email.constants.SendEmailConstants;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;
import org.subethamail.smtp.helper.SimpleMessageListener;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Arrays.asList;

public class EmailChannelBundleIT extends BaseOsgiIT implements SimpleMessageListener, WaitCondition {

    private final Object lock = new Object();

    private boolean messageReceived;
    private String receivedMessageText;

    public void testEmailSentOnSendEmailEvent() throws MessagingException, IOException, InterruptedException {

        SMTPServer smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(this));

        try {
            smtpServer.setPort(8099);
            smtpServer.start();

            String messageText = "test message";
            String from = "testfromaddress";
            String to = "testtoaddress";
            String subject = "test subject";

            Map<String, Object> values = new HashMap<>();
            values.put("fromAddress", from);
            values.put("toAddress", to);
            values.put("message", messageText);
            values.put("subject", subject);

            EventRelay eventRelay = applicationContext.getBean(EventRelay.class);
            assertNotNull(eventRelay);

            eventRelay.sendEventMessage(new MotechEvent(SendEmailConstants.SEND_EMAIL_SUBJECT, values));

            new Wait(lock, this, 100, 60000).start();

            assertTrue("Message not received", messageReceived);
            assertNotNull(receivedMessageText);
            assertEquals(messageText, receivedMessageText.trim());
        } finally {
            smtpServer.stop();
        }
    }


    @Override
    protected List<String> getImports() {
        return asList(
                "org.springframework.mail.javamail",
                "org.motechproject.email.service",
                "org.motechproject.security.model",
                "org.motechproject.commons.sql.service"
        );
    }


    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testblueprint.xml"};
    }

    @Override
    public boolean accept(String from, String recipient) {
        return true;
    }

    @Override
    public void deliver(String from, String recipient, InputStream data) throws IOException {
        messageReceived = true;

        try {
            MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()), data);
            receivedMessageText = String.valueOf(mimeMessage.getContent());
        } catch (MessagingException e) {
            throw new MotechException("Unable to parse message", e);
        }

        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public boolean needsToWait() {
        return !messageReceived;
    }
}
