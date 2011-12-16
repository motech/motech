package org.motechproject.sms.smpp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.smpp.constants.EventKeys;
import org.motechproject.sms.smpp.constants.SmsProperties;
import org.smslib.AGateway;
import org.smslib.OutboundMessage;

import java.util.Map;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutboundNotificationTest {
	@Mock
	private AGateway gateway;
	@Mock
	private OutboundEventGateway outboundEventGateway;

	private OutboundNotification outboundNotification;

	@Before
	public void setUp() {
		initMocks(this);
		Properties smsProperties = new Properties() {{
			setProperty(SmsProperties.MAX_RETRIES, "4");
		}};
		outboundNotification = new OutboundNotification(outboundEventGateway, smsProperties);
	}

	@Test
	public void shouldRaiseAnEventIfMessageDispatchHasFailedAfterMaxNumberOfRetries() {
		OutboundMessage message = new OutboundMessage() {{
			setRecipient("9876543210");
			setText("Test Message");
			setMessageStatus(OutboundMessage.MessageStatuses.FAILED);
			setRetryCount(4);
		}};

		outboundNotification.process(gateway, message);

		ArgumentCaptor<MotechEvent> motechEventArgumentCaptor = ArgumentCaptor.forClass(MotechEvent.class);
		verify(outboundEventGateway).sendEventMessage(motechEventArgumentCaptor.capture());

		Map<String, Object> parameters = motechEventArgumentCaptor.getValue().getParameters();
		assertEquals("9876543210", parameters.get(EventKeys.RECIPIENT));
		assertEquals("Test Message", parameters.get(EventKeys.MESSAGE));
	}

	@Test
	public void shouldNotRaiseAnEventIfMessageDispatchHasFailedAndIsGoingToBeRetried() {
		OutboundMessage message = new OutboundMessage() {{
			setRecipient("9876543210");
			setText("Test Message");
			setMessageStatus(OutboundMessage.MessageStatuses.FAILED);
			setRetryCount(1);
		}};

		outboundNotification.process(gateway, message);

		verifyZeroInteractions(outboundEventGateway);
	}

	@Test
	public void shouldNotRaiseAnyEventIfMessageDispatchIsSuccessful() {
		OutboundMessage message = new OutboundMessage() {{
			setMessageStatus(OutboundMessage.MessageStatuses.SENT);
			setRecipient("9876543210");
			setText("Test Message");
		}};

		outboundNotification.process(gateway, message);

		verifyZeroInteractions(outboundEventGateway);
	}
}
