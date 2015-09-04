package org.motechproject.email.it;

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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

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
public class EmailRecordServiceBundleIT extends BasePaxIT {

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

    @Test
    public void shouldAddAndUpdateEmail() {
        final DeliveryStatus deliveryStatus = DeliveryStatus.SENT;
        final String fromAddress = "f@adr";
        final String toAddress = "t@adr";
        final String subject = "test-subject";
        final String message = "test-message";
        final DateTime messageTime = DateUtil.now().toDateTime(DateTimeZone.UTC);

        emailRecordService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                EmailRecord expected = new EmailRecord(fromAddress, toAddress, subject, message, messageTime, deliveryStatus);
                emailRecordService.create(expected);

                List<EmailRecord> emailRecords = emailRecordService.retrieveAll();

                assertEquals(asList(expected), emailRecords);

                EmailRecord actual = emailRecords.get(0);

                actual.setMessage("test-newmessage");

                emailRecordService.update(actual);

                emailRecords = emailRecordService.retrieveAll();

                assertEquals(asList(actual), emailRecords);
            }
        });
    }

    @After
    public void tearDown() {
        emailRecordService.deleteAll();
    }
}
