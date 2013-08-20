package org.motechproject.email.repository;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.service.EmailRecordSearchCriteria;
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
public class AllEmailRecordsTest {

    @Autowired
    private AllEmailRecords allEmailRecords;

    @Test
    public void shouldCreateEmail() {
        DeliveryStatus deliveryStatus = DeliveryStatus.SENT;
        String fromAddress = "f@adr";
        String toAddress = "t@adr";
        String subject = "test-subject";
        String message = "test-message";
        DateTime sentDate = DateUtil.now();

        EmailRecord emailRecord = new EmailRecord(fromAddress, toAddress, subject, message, sentDate, deliveryStatus);
        allEmailRecords.add(emailRecord);

        EmailRecord savedMessage = allEmailRecords.findLatestBy(toAddress);
        assertNotNull(savedMessage);
        assertEquals(savedMessage.getSubject(), subject);
        assertEquals(savedMessage.getMessage(), message);
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
        allEmailRecords.add(emailRecord);

        EmailRecord duplicateMessage = new EmailRecord(fromAddress, toAddress, subject, message, messageTime, deliveryStatus);
        allEmailRecords.add(duplicateMessage);

        List<EmailRecord> allMessages = allEmailRecords.findAllBy(new EmailRecordSearchCriteria().withToAddress(toAddress));
        assertThat(allMessages.size(), is(2));
    }

    @After
    public void tearDown() {
        allEmailRecords.removeAll();
    }
}
