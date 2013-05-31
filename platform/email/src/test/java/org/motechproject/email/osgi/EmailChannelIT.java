package org.motechproject.email.osgi;

import org.motechproject.email.constants.SendEmailConstants;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class EmailChannelIT extends BaseOsgiIT {

    public void testEmailSentOnSendEmailEvent() throws MessagingException, IOException, InterruptedException {

        Wiser wiser = new Wiser();
        try {
            wiser.setPort(8099);
            wiser.start();

            String from = "testfromaddress";
            String to = "testtoaddress";
            String messageText = "test message";
            String subject = "test subject";

            Map<String, Object> values = new HashMap<>();
            values.put("fromAddress", from);
            values.put("toAddress", to);
            values.put("message", messageText);
            values.put("subject", subject);

            EventRelay eventRelay = applicationContext.getBean(EventRelay.class);
            assertNotNull(eventRelay);

            eventRelay.sendEventMessage(new MotechEvent(SendEmailConstants.SEND_EMAIL_SUBJECT, values));

            synchronized (this) {
                this.wait(3000);
            }

            List<WiserMessage> messages = wiser.getMessages();
            assertNotNull(messages);
            assertTrue(messages.size() > 0);

            WiserMessage message = messages.get(0);
            String actualText = message.getMimeMessage().getContent().toString();

            assertEquals(messageText, actualText.trim());
        } finally {
            wiser.stop();
        }
    }


    @Override
    protected List<String> getImports() {
        return asList(
                "org.springframework.mail.javamail",
                "org.motechproject.email.service"
        );
    }


    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testblueprint.xml"};
    }


}
