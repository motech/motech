package org.motechproject.sms.repository;


import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.sms.InboundSMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationRepository.xml"})
public class AllInboundSMSIT {

    @Autowired
    private AllInboundSMS allInboundSMS;

    @Test
    public void shouldFindByPhoneNumber() {
        String phoneNumber = "1234567890";
        String messageContent = "Message Content";
        createInboundMessage(phoneNumber, messageContent, DateTime.now());

        List<InboundSMS> smses = allInboundSMS.findBy(phoneNumber);
        assertThat(smses.size(), is(1));
        assertThat(smses.get(0).getPhoneNumber(), is(phoneNumber));
        assertThat(smses.get(0).getMessageContent(), is(messageContent));
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
        assertThat(allInboundSMS.messagesReceivedBetween(receivedTime, receivedTime).get(0).getPhoneNumber(), is(phoneNumber));
    }

    private void createInboundMessage(String phoneNumber, String messageContent, DateTime receivedTime) {
        allInboundSMS.add(new InboundSMS(phoneNumber, messageContent, receivedTime));
    }


    @After
    public void tearDown() {
        allInboundSMS.removeAll();
    }
}
