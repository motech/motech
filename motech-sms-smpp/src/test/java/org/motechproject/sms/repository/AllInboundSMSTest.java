package org.motechproject.sms.repository;


import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.sms.InboundSMS;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationRepository.xml"})
public class AllInboundSMSTest {

    @Autowired
    private AllInboundSMS allInboundSMS;

    @Test
    public void shouldCreateInboundMessageIdempotently() {
        Date dateTime = DateUtil.now().toDate();
        String recipient = "recipient";
        String content = "messageContent";
        String uuid = "3ae45-400a";
        InboundSMS inboundSMS = new InboundSMS(recipient, content, dateTime, uuid);
        allInboundSMS.createOrReplace(inboundSMS);

        InboundSMS duplicateMessage = new InboundSMS(recipient, content, dateTime, uuid);
        allInboundSMS.createOrReplace(duplicateMessage);

        List<InboundSMS> smsList = allInboundSMS.findAllBy(recipient, uuid);
        assertThat(smsList.size(), is(1));
    }

    @After
    public void tearDown() {
        allInboundSMS.removeAll();
    }


}
