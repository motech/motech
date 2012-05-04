package org.motechproject.sms.repository;

import ch.lambdaj.Lambda;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.sms.OutboundSMS;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static ch.lambdaj.Lambda.on;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.motechproject.util.DateUtil.time;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationRepository.xml"})
public class AllOutboundSMSIT {
    @Autowired
    private AllOutboundSMS allOutboundSMS;

    @Test
    public void shouldCreateOutboundSMS() {
        DeliveryStatus deliveryStatus = DeliveryStatus.INPROGRESS;
        String refNo = "refNo";
        String recipient =  "9123456780";
        String messageContent = "Dummy Message";
        DateTime sentDate = DateUtil.now();

        OutboundSMS outboundSMS = new OutboundSMS(recipient, refNo, messageContent, sentDate.toLocalDate(), time(sentDate), deliveryStatus);
        allOutboundSMS.createOrReplace(outboundSMS);

        OutboundSMS savedMessage = allOutboundSMS.findBy(refNo, recipient);
        assertEquals(savedMessage.getMessageContent(), messageContent);
    }
    
    @Test
    public void shouldCreateMessagesIdempotently() {
        DeliveryStatus deliveryStatus = DeliveryStatus.INPROGRESS;
        String refNo = "refNo";
        String recipient =  "9123456780";
        String messageContent = "Dummy Message";
        DateTime sentDate = DateUtil.now();

        OutboundSMS outboundSMS = new OutboundSMS(recipient, refNo, messageContent, sentDate.toLocalDate(), time(sentDate), deliveryStatus);
        allOutboundSMS.createOrReplace(outboundSMS);
        OutboundSMS duplicateMessage = new OutboundSMS(recipient, refNo, messageContent, sentDate.toLocalDate(), time(sentDate), deliveryStatus);
        allOutboundSMS.createOrReplace(duplicateMessage);

        List<OutboundSMS> allMessages = allOutboundSMS.findAllBy(refNo, recipient);
        assertThat(allMessages.size(), is(1));
    }

    @Test
    public void shouldFetchMessagesSentBetweenATimeRange() {
        String refNo = "refNo";
        String recipient =  "9123456780";
        String messageContent = "Dummy Message";
        DateTime sentDate = DateUtil.now();
        allOutboundSMS.createOrReplace(new OutboundSMS(recipient, refNo, messageContent, sentDate.toLocalDate(), time(sentDate), DeliveryStatus.INPROGRESS));
        allOutboundSMS.createOrReplace(new OutboundSMS("1234567890", refNo, messageContent + "1234",  sentDate.toLocalDate(), time(sentDate.minusMinutes(10)), DeliveryStatus.DELIVERED));
        allOutboundSMS.createOrReplace(new OutboundSMS("0986432112", refNo, messageContent + "5678",  sentDate.toLocalDate(), time(sentDate.minusHours(2)), DeliveryStatus.KEEPTRYING));

        List<OutboundSMS> outboundSMSes = allOutboundSMS.messagesSentBetween(sentDate.withTime(sentDate.getHourOfDay(), sentDate.getMinuteOfHour(), 0, 0), sentDate);
        assertThat(outboundSMSes.get(0).getPhoneNumber(), is(recipient));
        assertThat(outboundSMSes.get(0).getMessageContent(), is(messageContent));

        outboundSMSes = allOutboundSMS.messagesSentBetween(sentDate.minusMinutes(30), sentDate);
        List<String> phoneNumbers = Lambda.extract(outboundSMSes, on(OutboundSMS.class).getPhoneNumber());
        assertThat(phoneNumbers, hasItem(recipient));
        assertThat(phoneNumbers, hasItem("1234567890"));

        assertThat(allOutboundSMS.messagesSentBetween(sentDate, sentDate.minusMinutes(30)).size(), is(0));
    }

    @Test
    public void shouldUpdateTheDeliveryStatusForLatestRecordForMatchingRefNoForASubscriber() {
        String refNo = "refNo";
        String recipient =  "9123456780";
        DateTime sentDate = DateUtil.now();

        allOutboundSMS.createOrReplace(new OutboundSMS(recipient, refNo, "LatestMessage", sentDate.toLocalDate(), time(sentDate), DeliveryStatus.INPROGRESS));
        allOutboundSMS.createOrReplace(new OutboundSMS(recipient, refNo, "OlderMessage", sentDate.toLocalDate(), time(sentDate.minusMinutes(2)), DeliveryStatus.INPROGRESS));

        allOutboundSMS.updateDeliveryStatus(recipient, refNo, sentDate, DeliveryStatus.DELIVERED.name());

        List<OutboundSMS> smses = allOutboundSMS.findAllBy(refNo, recipient);
        assertThat(smses.get(0).getMessageContent(), is("LatestMessage"));
    }

    @After
    public void tearDown() {
        allOutboundSMS.removeAll();
    }
}
