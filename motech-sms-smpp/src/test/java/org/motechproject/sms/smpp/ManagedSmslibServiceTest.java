package org.motechproject.sms.smpp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.smslib.*;
import org.smslib.smpp.jsmpp.JSMPPGateway;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ManagedSmslibServiceTest {

	@Mock
	private Service smslibService;

    private Properties properties;

    @Before
	public void setup() {
		initMocks(this);
        properties = new Properties();
        properties.setProperty("host", "smppserver.com");
        properties.setProperty("port", "8876");
        properties.setProperty("system_id", "pavel");
        properties.setProperty("password", "wpsd");
	}

    @Test
    public void shouldConnectOnApplicationStartup() throws NoSuchMethodException {
        Method connect = ManagedSmslibService.class.getDeclaredMethod("connect", new Class[]{});
        assertTrue("PostConstruct annotation missing", connect.isAnnotationPresent(PostConstruct.class));
    }

    @Test
    public void shouldDisconnectOnApplicationShutdown() throws NoSuchMethodException {
        Method disconnect = ManagedSmslibService.class.getDeclaredMethod("disconnect", new Class[]{});
        assertTrue("PreDestroy annotation missing", disconnect.isAnnotationPresent(PreDestroy.class));
    }

    @Test
    public void shouldAddConfiguredJsmppGatewayDuringInitialization() throws GatewayException {
        new ManagedSmslibService(smslibService, properties);

        ArgumentCaptor<JSMPPGateway> jsmppGatewayCaptor = ArgumentCaptor.forClass(JSMPPGateway.class);
        verify(smslibService).addGateway(jsmppGatewayCaptor.capture());

        JSMPPGateway gateway = jsmppGatewayCaptor.getValue();
        assertEquals("smppserver.com", gateway.getHost());
        assertEquals(8876, gateway.getPort());
        assertEquals("pavel", gateway.getBindAttributes().getSystemId());
        assertEquals("wpsd", gateway.getBindAttributes().getPassword());
    }

	@Test
	public void shouldEstablishSmppConnection() throws SMSLibException, IOException, InterruptedException {
		ManagedSmslibService managedSmslibService = new ManagedSmslibService(smslibService, properties);
		managedSmslibService.connect();
		verify(smslibService).startService();
	}

	@Test
	public void shouldTerminateSmppConnection() throws IOException, SMSLibException, InterruptedException {
		ManagedSmslibService managedSmslibService = new ManagedSmslibService(smslibService, properties);
        managedSmslibService.disconnect();
		verify(smslibService).stopService();
	}

    @Test
    public void shouldSendSmsAsynchronously() throws GatewayException, IOException, TimeoutException, InterruptedException {
        ManagedSmslibService managedSmslibService = new ManagedSmslibService(smslibService, properties);
        managedSmslibService.queueMessage(Arrays.asList("recipient1", "recipient2"), "message");

        ArgumentCaptor groupNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(smslibService).createGroup((String) groupNameCaptor.capture());

        verify(smslibService).addToGroup((String) groupNameCaptor.getValue(), "recipient1");
        verify(smslibService).addToGroup((String) groupNameCaptor.getValue(), "recipient2");

        ArgumentCaptor<OutboundMessage> outboundMessageCaptor = ArgumentCaptor.forClass(OutboundMessage.class);
        verify(smslibService).queueMessage(outboundMessageCaptor.capture());

        assertEquals("message", outboundMessageCaptor.getValue().getText());
        assertEquals(groupNameCaptor.getValue(), outboundMessageCaptor.getValue().getRecipient());

        verify(smslibService).removeGroup((String) groupNameCaptor.getValue());
    }
}
