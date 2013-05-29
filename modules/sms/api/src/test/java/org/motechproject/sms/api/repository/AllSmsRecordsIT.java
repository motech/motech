package org.motechproject.sms.api.repository;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.sms.api.SMSType;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsRecordSearchCriteria;
import org.motechproject.sms.api.domain.SmsRecords;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.motechproject.sms.api.DeliveryStatus.ABORTED;
import static org.motechproject.sms.api.DeliveryStatus.DELIVERED;
import static org.motechproject.sms.api.DeliveryStatus.DELIVERY_CONFIRMED;
import static org.motechproject.sms.api.DeliveryStatus.DISPATCHED;
import static org.motechproject.sms.api.DeliveryStatus.INPROGRESS;
import static org.motechproject.sms.api.DeliveryStatus.KEEPTRYING;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationSmsApi.xml"})
public class AllSmsRecordsIT {

    @Autowired
    private AllSmsRecords allSmsRecords;

    @Test
    public void shouldCreateOutboundSMS() {
        DeliveryStatus deliveryStatus = INPROGRESS;
        String refNo = "refNo";
        String recipient = "9123456780";
        String messageContent = "Dummy Message";
        DateTime sentDate = DateUtil.now();

        SmsRecord smsRecord = new SmsRecord(SMSType.OUTBOUND, recipient, messageContent, sentDate, deliveryStatus, refNo);
        allSmsRecords.addOrReplace(smsRecord);

        SmsRecord savedMessage = allSmsRecords.findLatestBy(recipient, refNo);
        assertNotNull(savedMessage);
        assertEquals(savedMessage.getMessageContent(), messageContent);
    }

    @Test
    public void shouldFindLatestOutboundSMSForDuplicateRecords() {
        String refNo = "refNo";
        String recipient = "9123456780";
        String messageContent = "Dummy Message";
        DateTime messageTime = DateUtil.now().toDateTime(DateTimeZone.UTC);

        allSmsRecords.addOrReplace(new SmsRecord(SMSType.OUTBOUND, recipient, messageContent, messageTime, DELIVERED, refNo));
        allSmsRecords.addOrReplace(new SmsRecord(SMSType.OUTBOUND, recipient, messageContent, messageTime.plusDays(1), ABORTED, refNo));
        allSmsRecords.addOrReplace(new SmsRecord(SMSType.OUTBOUND, recipient, messageContent, messageTime.plusDays(3), INPROGRESS, refNo));

        SmsRecord latest = allSmsRecords.findLatestBy(recipient, refNo);
        assertThat(latest.getMessageTime(), is(messageTime.plusDays(3)));
        assertThat(latest.getDeliveryStatus(), is(DISPATCHED));

        allSmsRecords.addOrReplace(new SmsRecord(SMSType.OUTBOUND, recipient, messageContent, messageTime.plusDays(6).plusMinutes(4), KEEPTRYING, refNo));
        allSmsRecords.addOrReplace(new SmsRecord(SMSType.OUTBOUND, recipient, messageContent, messageTime.plusHours(2), INPROGRESS, refNo));

        latest = allSmsRecords.findLatestBy(recipient, refNo);
        assertThat(latest.getMessageTime(), is(messageTime.plusDays(6).plusMinutes(4)));
        assertThat(latest.getDeliveryStatus(), is(KEEPTRYING));
    }

    @Test
    //TODO: Acc to the SMSLib documentation, refNo and recipient combination may not be always unique
    public void shouldCreateIdempotentMessages() {
        SMSType smsType = SMSType.OUTBOUND;
        DeliveryStatus deliveryStatus = INPROGRESS;
        String refNo = "refNo";
        String recipient = "9123456780";
        String messageContent = "Dummy Message";
        DateTime messageTime = DateUtil.now().toDateTime(DateTimeZone.UTC);

        SmsRecord smsRecord = new SmsRecord(smsType, recipient, messageContent, messageTime, deliveryStatus, refNo);
        allSmsRecords.addOrReplace(smsRecord);

        SmsRecord duplicateMessage = new SmsRecord(smsType, recipient, messageContent, messageTime, deliveryStatus, refNo);
        allSmsRecords.addOrReplace(duplicateMessage);

        SmsRecords allMessages = allSmsRecords.findAllBy(new SmsRecordSearchCriteria().withReferenceNumber(refNo).withPhoneNumber(recipient));
        assertThat(allMessages.getRecords().size(), is(1));
    }

    @Test
    public void shouldUpdateTheDeliveryStatusForLatestRecordForMatchingRefNoForASubscriber() {
        String refNo = "refNo";
        String recipient = "9123456780";
        DateTime sentDate = DateUtil.now();

        final SmsRecord latestMessage = new SmsRecord(SMSType.OUTBOUND, recipient, "LatestMessage", sentDate, INPROGRESS, refNo);
        allSmsRecords.addOrReplace(latestMessage);
        allSmsRecords.addOrReplace(new SmsRecord(SMSType.OUTBOUND, recipient, "OlderMessage", sentDate.minusMinutes(2), INPROGRESS, refNo));

        allSmsRecords.updateDeliveryStatus(recipient, refNo, DELIVERED.name());

        SmsRecord updatedSms = allSmsRecords.get(latestMessage.getId());
        assertThat(updatedSms.getMessageContent(), is("LatestMessage"));
        assertThat(updatedSms.getDeliveryStatus(), is(DELIVERY_CONFIRMED));
    }

    @After
    public void tearDown() {
        allSmsRecords.removeAll();
    }
}
