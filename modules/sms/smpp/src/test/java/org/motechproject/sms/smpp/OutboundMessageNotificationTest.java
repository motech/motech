package org.motechproject.sms.smpp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.motechproject.sms.smpp.constants.SmsProperties;
import org.smslib.AGateway;
import org.smslib.OutboundMessage;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.sms.api.constants.EventDataKeys.MESSAGE;
import static org.motechproject.sms.smpp.constants.EventDataKeys.RECIPIENT;

public class OutboundMessageNotificationTest {
    @Mock
    private AGateway gateway;
    @Mock
    private EventRelay eventRelay;

    private OutboundMessageNotification outboundMessageNotification;
    @Mock
    private SmsAuditService smsAuditService;

    @Before
    public void setUp() {
        initMocks(this);

        Properties smsProperties = new Properties() {{
            setProperty(SmsProperties.MAX_RETRIES, "4");
        }};
        SettingsFacade settings = new SettingsFacade();
        settings.saveConfigProperties("sms.properties", smsProperties);

        outboundMessageNotification = new OutboundMessageNotification(eventRelay, settings);
        ReflectionTestUtils.setField(outboundMessageNotification, "smsAuditService", smsAuditService);
    }

    @Test
    public void shouldRaiseAnEventIfMessageDispatchHasFailedAfterMaxNumberOfRetries() {
        final String recipient = "9876543210";
        String myText = "Test Message";
        OutboundMessage message = new OutboundMessage(recipient, myText) {{
            setRefNo("refNo11111");
            setMessageStatus(OutboundMessage.MessageStatuses.FAILED);
            setRetryCount(4);
        }};


        outboundMessageNotification.process(gateway, message);

        ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(motechEventArgumentCaptor.capture());
        Map<String, Object> parameters = motechEventArgumentCaptor.getValue().getParameters();
        assertEquals(recipient, parameters.get(RECIPIENT));
        assertEquals("Test Message", parameters.get(MESSAGE));

        assertAuditMessage(recipient, "refNo11111", DeliveryStatus.ABORTED);
    }

    @Test
    public void shouldNotRaiseAnEventIfMessageDispatchHasFailedAndIsGoingToBeRetried() {
        String recipient = "9876543210";
        String myText = "Test Message";
        OutboundMessage message = new OutboundMessage(recipient, myText) {{
            setRefNo("refNo2222");
            setMessageStatus(OutboundMessage.MessageStatuses.FAILED);
            setRetryCount(1);
        }};

        outboundMessageNotification.process(gateway, message);

        verifyZeroInteractions(eventRelay);
        assertAuditMessage(recipient, "refNo2222", DeliveryStatus.KEEPTRYING);
    }

    @Test
    public void shouldNotRaiseAnyEventIfMessageDispatchIsSuccessful() {
        String recipient = "9876543210";
        String myText = "Test Message";
        OutboundMessage message = new OutboundMessage(recipient, myText) {{
            setMessageStatus(OutboundMessage.MessageStatuses.SENT);
            setRefNo("refNo");
        }};

        outboundMessageNotification.process(gateway, message);

        verifyZeroInteractions(eventRelay);
        assertAuditMessage(recipient, "refNo", DeliveryStatus.DISPATCHED);
    }

    private void assertAuditMessage(String recipient, String refNo, DeliveryStatus deliveryStatus) {
        ArgumentCaptor<SmsRecord> outboundSMSCaptor = ArgumentCaptor.forClass(SmsRecord.class);

        verify(smsAuditService).log(outboundSMSCaptor.capture());
        assertEquals(refNo, outboundSMSCaptor.getValue().getReferenceNumber());
        assertEquals(deliveryStatus, outboundSMSCaptor.getValue().getDeliveryStatus());
        assertEquals(recipient, outboundSMSCaptor.getValue().getPhoneNumber());
    }
}
