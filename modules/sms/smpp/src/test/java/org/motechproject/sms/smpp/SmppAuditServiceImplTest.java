package org.motechproject.sms.smpp;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.sms.smpp.repository.AllInboundSMS;
import org.motechproject.sms.smpp.repository.AllOutboundSMS;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;

public class SmppAuditServiceImplTest {

    @Mock
    private AllOutboundSMS allOutboundSMS;
    @Mock
    private AllInboundSMS allInboundSMS;
    SmppAuditServiceImpl smppAuditService;

    @Before
    public void setUp() {
        initMocks(this);
        smppAuditService = new SmppAuditServiceImpl(allOutboundSMS, allInboundSMS);
    }

    @Test
    public void shouldFetchAllInbound() {
        final String phoneNumber = "23423423";
        final DateTime from = newDateTime(2012, 2, 3, 4, 5, 6);
        final DateTime to = newDateTime(2012, 3, 4, 2, 1, 4);
        smppAuditService.allInboundMessagesBetween(phoneNumber, from, to);
        verify(allInboundSMS).messagesReceivedBetween(phoneNumber, from, to);
    }

    @Test
    public void shouldFetchAllOutbound() {
        final String phoneNumber = "23423423";
        final DateTime from = newDateTime(2012, 2, 3, 4, 5, 6);
        final DateTime to = newDateTime(2012, 3, 4, 2, 1, 4);
        smppAuditService.allOutboundMessagesBetween(phoneNumber, from, to);
        verify(allOutboundSMS).messagesSentBetween(phoneNumber, from, to);
    }
}
