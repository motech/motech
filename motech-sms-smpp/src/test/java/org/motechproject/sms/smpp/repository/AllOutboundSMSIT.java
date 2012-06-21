package org.motechproject.sms.smpp.repository;

import ch.lambdaj.Lambda;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.sms.smpp.OutboundSMS;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.motechproject.sms.api.DeliveryStatus.ABORTED;
import static org.motechproject.sms.api.DeliveryStatus.DELIVERED;
import static org.motechproject.sms.api.DeliveryStatus.INPROGRESS;
import static org.motechproject.sms.api.DeliveryStatus.KEEPTRYING;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationRepository.xml"})
public class AllOutboundSMSIT {
    @Autowired
    private AllOutboundSMS allOutboundSMS;

    @Test
    public void shouldCreateOutboundSMS() {
        DeliveryStatus deliveryStatus = INPROGRESS;
        String refNo = "refNo";
        String recipient = "9123456780";
        String messageContent = "Dummy Message";
        DateTime sentDate = DateUtil.now();

        OutboundSMS outboundSMS = new OutboundSMS(recipient, refNo, messageContent, sentDate, deliveryStatus);
        allOutboundSMS.createOrReplace(outboundSMS);

        OutboundSMS savedMessage = allOutboundSMS.findLatestBy(refNo, recipient);
        assertEquals(savedMessage.getMessageContent(), messageContent);
    }

    @Test
    public void shouldFindLatestOutboundSMSForDuplicateRecords() {
        String refNo = "refNo";
        String recipient = "9123456780";
        String messageContent = "Dummy Message";
        DateTime sentDate = DateUtil.now();

        allOutboundSMS.createOrReplace(new OutboundSMS(recipient, refNo, messageContent, sentDate, DELIVERED));
        allOutboundSMS.createOrReplace(new OutboundSMS(recipient, refNo, messageContent, sentDate.plusDays(1), ABORTED));
        allOutboundSMS.createOrReplace(new OutboundSMS(recipient, refNo, messageContent, sentDate.plusDays(3), INPROGRESS));

        OutboundSMS latest = allOutboundSMS.findLatestBy(refNo, recipient);
        assertThat(latest.getMessageTime(), is(sentDate.plusDays(3)));
        assertThat(latest.getDeliveryStatus(), is(INPROGRESS));

        allOutboundSMS.createOrReplace(new OutboundSMS(recipient, refNo, messageContent, sentDate.plusDays(6).plusMinutes(4), KEEPTRYING));
        allOutboundSMS.createOrReplace(new OutboundSMS(recipient, refNo, messageContent, sentDate.plusHours(2), INPROGRESS));

        latest = allOutboundSMS.findLatestBy(refNo, recipient);
        assertThat(latest.getMessageTime(), is(sentDate.plusDays(6).plusMinutes(4)));
        assertThat(latest.getDeliveryStatus(), is(KEEPTRYING));
    }

    @Test
    public void shouldCreateMessagesIdempotently() {
        DeliveryStatus deliveryStatus = INPROGRESS;
        String refNo = "refNo";
        String recipient = "9123456780";
        String messageContent = "Dummy Message";
        DateTime sentDate = DateUtil.now();

        OutboundSMS outboundSMS = new OutboundSMS(recipient, refNo, messageContent, sentDate, deliveryStatus);
        allOutboundSMS.createOrReplace(outboundSMS);
        OutboundSMS duplicateMessage = new OutboundSMS(recipient, refNo, messageContent, sentDate, deliveryStatus);
        allOutboundSMS.createOrReplace(duplicateMessage);

        List<OutboundSMS> allMessages = allOutboundSMS.findAllBy(refNo, recipient);
        assertThat(allMessages.size(), is(1));
    }

    @Test
    public void shouldFetchMessagesSentBetweenATimeRange() {
        String refNo = "refNo";
        String recipient = "9123456780";
        DateTime sentDate = DateUtil.now();
        allOutboundSMS.createOrReplace(new OutboundSMS(recipient, refNo, "Dummy Message", sentDate, INPROGRESS));
        allOutboundSMS.createOrReplace(new OutboundSMS("1234567890", refNo, "Dummy Message 1234", sentDate.minusMinutes(10), DELIVERED));
        allOutboundSMS.createOrReplace(new OutboundSMS("0986432112", refNo, "Dummy Message 5678", sentDate.minusHours(2), KEEPTRYING));

        List<OutboundSMS> outboundSMSes = allOutboundSMS.messagesSentBetween(sentDate.withTime(sentDate.getHourOfDay(), sentDate.getMinuteOfHour(), 0, 0), sentDate);
        assertThat(outboundSMSes.get(0).getPhoneNumber(), is(recipient));

        assertThat(allOutboundSMS.messagesSentBetween(sentDate.minusHours(2), sentDate.minusHours(1)).get(0).getPhoneNumber(), is("0986432112"));

        outboundSMSes = allOutboundSMS.messagesSentBetween(sentDate.minusMinutes(30), sentDate);
        List<String> phoneNumbers = Lambda.extract(outboundSMSes, on(OutboundSMS.class).getPhoneNumber());
        assertThat(phoneNumbers, hasItem(recipient));
        assertThat(phoneNumbers, hasItem("1234567890"));

    }

    @Test
    public void shouldFetchMessagesSentBetweenATimeRangeForAPhone() {
        String refNo = "refNo";
        String recipient = "9123456780";
        DateTime sentDate = DateUtil.now();
        allOutboundSMS.createOrReplace(new OutboundSMS(recipient, refNo, "Dummy Message", sentDate, INPROGRESS));
        allOutboundSMS.createOrReplace(new OutboundSMS(recipient, refNo, "Dummy Message 1234", sentDate.minusMinutes(10), DELIVERED));
        allOutboundSMS.createOrReplace(new OutboundSMS("0986432112", refNo, "Dummy Message 5678", sentDate.minusHours(2), KEEPTRYING));

        List<OutboundSMS> outboundSMSes = allOutboundSMS.messagesSentBetween(recipient, sentDate.withTime(sentDate.getHourOfDay(), sentDate.getMinuteOfHour(), 0, 0), sentDate);
        assertThat(outboundSMSes.get(0).getPhoneNumber(), is(recipient));
        assertThat(outboundSMSes.get(0).getMessageContent(), is("Dummy Message"));

        assertThat(allOutboundSMS.messagesSentBetween("0986432112", sentDate.withTime(sentDate.getHourOfDay(), sentDate.getMinuteOfHour(), 0, 0), sentDate), is(Collections.<OutboundSMS>emptyList()));

        outboundSMSes = allOutboundSMS.messagesSentBetween(recipient, sentDate.minusMinutes(30), sentDate);
        List<String> phoneNumbers = Lambda.extract(outboundSMSes, on(OutboundSMS.class).getPhoneNumber());
        assertThat(phoneNumbers, is(asList(recipient, recipient)));

    }

    @Test
    public void shouldUpdateTheDeliveryStatusForLatestRecordForMatchingRefNoForASubscriber() {
        String refNo = "refNo";
        String recipient = "9123456780";
        DateTime sentDate = DateUtil.now();

        final OutboundSMS latestMessage = new OutboundSMS(recipient, refNo, "LatestMessage", sentDate, INPROGRESS);
        allOutboundSMS.createOrReplace(latestMessage);
        allOutboundSMS.createOrReplace(new OutboundSMS(recipient, refNo, "OlderMessage", sentDate.minusMinutes(2), INPROGRESS));

        allOutboundSMS.updateDeliveryStatus(recipient, refNo, DELIVERED.name());

        OutboundSMS updatedOutboundSms = allOutboundSMS.get(latestMessage.getId());
        assertThat(updatedOutboundSms.getMessageContent(), is("LatestMessage"));
        assertThat(updatedOutboundSms.getDeliveryStatus(), is(DELIVERED));
    }

    @After
    public void tearDown() {
        allOutboundSMS.removeAll();
    }
}
