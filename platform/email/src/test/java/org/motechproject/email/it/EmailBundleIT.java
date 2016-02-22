package org.motechproject.email.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.email.domain.Mail;
import org.motechproject.email.exception.EmailSendException;
import org.motechproject.email.service.EmailRecordService;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class EmailBundleIT extends BasePaxIT {

    @Inject @org.ops4j.pax.exam.util.Filter(timeout=360000)
    private EmailSenderService mailService;

    @Inject
    private EmailRecordService emailRecordService;

    private Wiser smtpServer;

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
    public void testEmailService() throws MessagingException, IOException, EmailSendException {
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            mailService.send("from@from.com", "to@to.com", "test", "test");
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }

        WiserMessage message = smtpServer.getMessages().get(0);
        String msgTxt = (String) message.getMimeMessage().getContent();

        assertNotNull(msgTxt);
        assertEquals("test", msgTxt.trim());
    }

    @After
    public void tearDown() {
        smtpServer.stop();
        emailRecordService.deleteAll();
    }
}
