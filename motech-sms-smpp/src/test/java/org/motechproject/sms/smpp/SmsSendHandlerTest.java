package org.motechproject.sms.smpp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.constants.EventSubject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.sms.api.service.SmsServiceImpl.MESSAGE;
import static org.motechproject.sms.api.service.SmsServiceImpl.RECIPIENTS;

public class SmsSendHandlerTest {
	private SmsSendHandler handler;

	@Mock
	private ManagedSmslibService managedSmslibService;

	@Before
	public void setup() {
		initMocks(this);
		handler = new SmsSendHandler(managedSmslibService);
	}

	@Test
	public void shouldListenToSmsSendEvent() throws NoSuchMethodException {
		Method handleMethod = SmsSendHandler.class.getDeclaredMethod("handle", new Class[]{MotechEvent.class});
		assertTrue("MotechListener annotation missing", handleMethod.isAnnotationPresent(MotechListener.class));
		MotechListener annotation = handleMethod.getAnnotation(MotechListener.class);
		assertArrayEquals(new String[]{EventSubject.SEND_SMS}, annotation.subjects());
	}

	@Test
	public void shouldSendMessageUsingSmpp() throws Exception {
		final String recipient = "0987654321";
		final String message = "foo bar";
		final List<String> recipients = Arrays.asList(recipient);

		handler = new SmsSendHandler(managedSmslibService);

		handler.handle(new MotechEvent(EventSubject.SEND_SMS, new HashMap<String, Object>() {{
			put(RECIPIENTS, recipients);
			put(MESSAGE, message);
		}}));

		verify(managedSmslibService).queueMessage(recipients, message);
	}
}
