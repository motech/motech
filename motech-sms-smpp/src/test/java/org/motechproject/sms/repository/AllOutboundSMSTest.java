package org.motechproject.sms.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.sms.DeliveryStatus;
import org.motechproject.sms.OutboundSMS;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationSmsSmpp.xml"})
public class AllOutboundSMSTest {
    @Autowired
    private AllOutboundSMS allOutboundSMS;

    @Test
    public void shouldCreateOutboundSMS() {
        DeliveryStatus deliveryStatus = DeliveryStatus.INPROGRESS;
        String refNo = "refNo";
        String recipient =  "9123456780";
        String messageContent = "Dummy Message";
        Date sentDate = DateUtil.now().toDate();

        OutboundSMS outboundSMS = new OutboundSMS(recipient, refNo, messageContent, sentDate, deliveryStatus);
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
        Date sentDate = DateUtil.now().toDate();

        OutboundSMS outboundSMS = new OutboundSMS(recipient, refNo, messageContent, sentDate, deliveryStatus);
        allOutboundSMS.createOrReplace(outboundSMS);
        OutboundSMS duplicateMessage = new OutboundSMS(recipient, refNo, messageContent, sentDate, deliveryStatus);
        allOutboundSMS.createOrReplace(duplicateMessage);

        List<OutboundSMS> allMessages = allOutboundSMS.findAllBy(refNo, recipient);
        assertThat(allMessages.size(), is(1));
    }

    @After
    public void tearDown() {
        allOutboundSMS.removeAll();
    }


}
