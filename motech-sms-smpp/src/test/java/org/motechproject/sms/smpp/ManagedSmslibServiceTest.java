package org.motechproject.sms.smpp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.smslib.*;
import org.smslib.smpp.jsmpp.JSMPPGateway;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ManagedSmslibServiceTest {

	@Mock
	private Service smslibService;

	@Before
	public void setup() {
		initMocks(this);
	}

	@Test
	public void shouldAddJsmppGatewayDuringInitialization() throws GatewayException {
		new ManagedSmslibService(smslibService);
		verify(smslibService).addGateway(Matchers.<JSMPPGateway>any());
	}

	@Test
	public void shouldSendSms() throws GatewayException, IOException, TimeoutException, InterruptedException {
		ManagedSmslibService managedSmslibService = new ManagedSmslibService(smslibService);
		managedSmslibService.sendMessage(Arrays.asList("recipient1", "recipient2", "recipient3"), "message");

		ArgumentCaptor groupNameCaptor = ArgumentCaptor.forClass(String.class);
		verify(smslibService).createGroup((String) groupNameCaptor.capture());

		ArgumentCaptor<OutboundMessage> outboundMessageCaptor = ArgumentCaptor.forClass(OutboundMessage.class);
		verify(smslibService).sendMessage(outboundMessageCaptor.capture());

		assertEquals("message", outboundMessageCaptor.getValue().getText());
		assertEquals(groupNameCaptor.getValue(), outboundMessageCaptor.getValue().getRecipient());

		verify(smslibService).removeGroup((String) groupNameCaptor.getValue());
	}

	@Test
	public void shouldEstablishSmppConnection() throws SMSLibException, IOException, InterruptedException {
		ManagedSmslibService managedSmslibService = new ManagedSmslibService(smslibService);
		managedSmslibService.connect();
		verify(smslibService).startService();
	}

	@Test
	public void shouldConnectOnApplicationStartup() throws NoSuchMethodException {
		Method connect = ManagedSmslibService.class.getDeclaredMethod("connect", new Class[]{});
		assertTrue("PostConstruct annotation missing", connect.isAnnotationPresent(PostConstruct.class));
	}
}
