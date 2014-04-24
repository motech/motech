package org.motechproject.email.osgi;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.MotechException;
import org.motechproject.email.constants.SendEmailConstants;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.osgi.wait.ContextPublishedWaitCondition;
import org.motechproject.testing.osgi.wait.Wait;
import org.motechproject.testing.osgi.wait.WaitCondition;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;
import org.subethamail.smtp.helper.SimpleMessageListener;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class EmailChannelBundleIT extends BasePaxIT implements SimpleMessageListener, WaitCondition {

    private final Object lock = new Object();

    private boolean messageReceived;
    private String receivedMessageText;

    @Inject
    private EventRelay eventRelay;

    @Inject
    private BundleContext bundleContext;

    @Override
    protected boolean shouldFakeModuleStartupEvent() {
        // We must start modules because of emails startup dependency on Scheduler.
        // TODO: This dependency will be removed during migrations
        return true;
    }

    @Override
    protected Collection<String> getAdditionalTestDependencies() {
        return Arrays.asList("org.subethamail:org.motechproject.org.subethamail");
    }

    @Test
    public void testEmailSentOnSendEmailEvent() throws MessagingException, IOException, InterruptedException {
        SMTPServer smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(this));

        new Wait(new ContextPublishedWaitCondition(bundleContext, "org.motechproject.motech-platform-event"), 5000).start();
        new Wait(new ContextPublishedWaitCondition(bundleContext), 5000).start();

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
    public boolean accept(String from, String recipient) {
        return true;
    }

    @Override
    public void deliver(String from, String recipient, InputStream data) throws IOException {
        messageReceived = true;
        try {
            MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()), data);
            try (InputStream in = (InputStream) mimeMessage.getContent()) {
                receivedMessageText = IOUtils.toString(in);
            }
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
