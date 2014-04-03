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
import org.motechproject.mds.util.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationEmail.xml"})
public class EmailRecordServiceIT {

    @Autowired
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

        List<EmailRecord> savedMessages = emailRecordService.findByRecipientAddress(toAddress,
                QueryParams.descOrder(QueryParams.MODIFICATION_DATE));
        assertNotNull(savedMessages);
        assertEquals(savedMessages.get(0).getSubject(), subject);
        assertEquals(savedMessages.get(0).getMessage(), message);
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

        List<EmailRecord> allMessages = emailRecordService.findByRecipientAddress(toAddress, null);
        assertThat(allMessages.size(), is(2));
    }

    @After
    public void tearDown() {
        emailRecordService.deleteAll();
    }
}
