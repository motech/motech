package org.motechproject.email.osgi;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class EmailBundleIT extends BasePaxIT {

   /* @Inject
    @Filter(timeout = 60000 * 7, value = "(objectclass=org.motechproject.email.service.EmailSenderService)")
    private EmailSenderService mailService;
*/
    @Inject
    private BundleContext bundleContext;

    private Wiser smtpServer;

    @Override
    protected String getDefaultLogLevel() {
        return "DEBUG";
    }

    @Override
    protected boolean startHttpServer() {
        return true;
    }

    @Override
    protected Collection<String> getAdditionalTestDependencies() {
        return Arrays.asList("org.subethamail:org.motechproject.org.subethamail");
    }

    @Before
    public void setUp() {
        smtpServer = new Wiser(8099);
        smtpServer.start();
    }

    @Test
    public void testEmailService() throws MessagingException, IOException, InterruptedException {
        Object srv = ServiceRetriever.getService(bundleContext, EmailSenderService.class);
        EmailSenderService mailService = (EmailSenderService) srv;

        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            mailService.send(new Mail("from@from.com", "to@to.com", "test", "test"));
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }

        WiserMessage message = smtpServer.getMessages().get(0);

        try (InputStream in = (InputStream) message.getMimeMessage().getContent()) {
            String actualText = IOUtils.toString(in);
            assertEquals("test", actualText.trim());
        }
    }

    @After
    public void onTearDown() {
        smtpServer.stop();
    }
}
