package org.motechproject.email.osgi;

import org.junit.After;
import org.junit.Before;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;

public class EmailBundleIT extends BaseOsgiIT {


    private Wiser smtpServer;

    @Before
    public void onSetUp() {
        smtpServer = new Wiser(8099);
        smtpServer.start();
    }

    public void testEmailService() throws MessagingException, IOException {

        EmailSenderService mailService = (EmailSenderService) applicationContext.getBean("emailSenderService");

        mailService.send(new Mail("from@from.com", "to@to.com", "test", "test"));

        WiserMessage message = smtpServer.getMessages().get(0);
        String actualText = message.getMimeMessage().getContent().toString();

        assertEquals("test", actualText.trim());

    }

    @After
    public void onTearDown() {
        smtpServer.stop();
    }


    @Override
    protected List<String> getImports() {
        return asList(
                "org.springframework.mail.javamail", "org.motechproject.event.listener"
        );
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testblueprint.xml"};
    }
}
