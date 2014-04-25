package org.motechproject.email.repository;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.service.EmailRecordService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class EmailRecordServiceIT extends BasePaxIT {

    @Inject
    private EmailRecordService emailRecordService;

    @Test
    public void shouldCreateEmail() {
        DeliveryStatus deliveryStatus = DeliveryStatus.SENT;
        String fromAddress = "f@adr";
        String toAddress = "t@adr";
        String subject = "test-subject";
        String message = "test-message";
        DateTime sentDate = DateUtil.now();

        EmailRecord emailRecord = new EmailRecord(fromAddress, toAddress, subject, message, sentDate, deliveryStatus);
        emailRecordService.create(emailRecord);

        List<EmailRecord> savedMessages = emailRecordService.findByRecipientAddress(toAddress);

        assertNotNull(savedMessages);
        assertEquals(asList(subject), extract(savedMessages, on(EmailRecord.class).getSubject()));
        assertEquals(asList(message), extract(savedMessages, on(EmailRecord.class).getMessage()));
    }

    @Test
    public void shouldCreateIdenticalMessages() {
        DeliveryStatus deliveryStatus = DeliveryStatus.SENT;
        String fromAddress = "f@adr";
        String toAddress = "t@adr";
        String subject = "test-subject";
        String message = "test-message";
        DateTime messageTime = DateUtil.now().toDateTime(DateTimeZone.UTC);

        EmailRecord emailRecord = new EmailRecord(fromAddress, toAddress, subject, message, messageTime, deliveryStatus);
        emailRecordService.create(emailRecord);

        EmailRecord duplicateMessage = new EmailRecord(fromAddress, toAddress, subject, message, messageTime, deliveryStatus);
        emailRecordService.create(duplicateMessage);

        List<EmailRecord> allMessages = emailRecordService.findByRecipientAddress(toAddress);
        assertEquals(2, allMessages.size());
    }

    @After
    public void tearDown() {
        emailRecordService.deleteAll();
    }
}
