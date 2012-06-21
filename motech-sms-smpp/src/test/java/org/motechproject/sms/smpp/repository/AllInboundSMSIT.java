package org.motechproject.sms.smpp.repository;


import ch.lambdaj.Lambda;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.sms.smpp.InboundSMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationRepository.xml"})
public class AllInboundSMSIT {

    @Autowired
    private AllInboundSMS allInboundSMS;

    @Test
    public void shouldFetchMessagesReceivedBetweenATimeRangeForPhoneNumber() {
        String phoneNumber = "1234567890";
        String messageContent = "Message Content";
        DateTime receivedTime = DateTime.now();
        createInboundMessage(phoneNumber, messageContent, receivedTime);
        createInboundMessage("4534535345", messageContent, receivedTime.minusDays(1));
        createInboundMessage("1234509876", messageContent, receivedTime.minusWeeks(1));

        List<InboundSMS> inboundSMSes = allInboundSMS.messagesReceivedBetween("1234509876", receivedTime.minusWeeks(2), receivedTime.minusDays(2));
        assertThat(inboundSMSes.size(), is(1));
        assertThat(inboundSMSes.get(0).getPhoneNumber(), is("1234509876"));

        assertThat(allInboundSMS.messagesReceivedBetween("4534535345", receivedTime.minusWeeks(2), receivedTime.minusDays(2)), is(Collections.<InboundSMS>emptyList()));
        assertThat(allInboundSMS.messagesReceivedBetween(phoneNumber, receivedTime.withTimeAtStartOfDay(), receivedTime).get(0).getPhoneNumber(), is(phoneNumber));
    }

    @Test
    public void shouldFetchMessagesReceivedBetweenATimeRange() {
        String phoneNumber = "1234567890";
        String messageContent = "Message Content";
        DateTime receivedTime = DateTime.now();
        createInboundMessage(phoneNumber, messageContent, receivedTime);
        createInboundMessage("4534535345", messageContent, receivedTime.minusDays(1));
        createInboundMessage("1234509876", messageContent, receivedTime.minusWeeks(1));

        List<InboundSMS> inboundSMSes = allInboundSMS.messagesReceivedBetween(receivedTime.minusWeeks(2), receivedTime.minusDays(2));
        assertThat(inboundSMSes.size(), is(1));
        assertThat(inboundSMSes.get(0).getPhoneNumber(), is("1234509876"));

        assertThat(allInboundSMS.messagesReceivedBetween(receivedTime.minusWeeks(3), receivedTime.minusWeeks(2).minusMinutes(1)), is(Collections.<InboundSMS>emptyList()));
        assertThat(allInboundSMS.messagesReceivedBetween(receivedTime.withTimeAtStartOfDay(), receivedTime).get(0).getPhoneNumber(), is(phoneNumber));
        final List<InboundSMS> actual = allInboundSMS.messagesReceivedBetween(receivedTime.minusWeeks(1), receivedTime);
        assertThat(Lambda.extract(actual, on(InboundSMS.class).getPhoneNumber()), hasItem(phoneNumber));
        assertThat(Lambda.extract(actual, on(InboundSMS.class).getPhoneNumber()), hasItem("4534535345"));
    }

    private void createInboundMessage(String phoneNumber, String messageContent, DateTime receivedTime) {
        allInboundSMS.add(new InboundSMS(phoneNumber, messageContent, receivedTime));
    }

    @After
    public void tearDown() {
        allInboundSMS.removeAll();
    }
}
