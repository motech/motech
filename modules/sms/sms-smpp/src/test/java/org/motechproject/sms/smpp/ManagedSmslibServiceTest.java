package org.motechproject.sms.smpp;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.scheduler.gateway.OutboundEventGateway;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.smpp.constants.SmppProperties;
import org.motechproject.sms.smpp.constants.SmsProperties;
import org.smslib.GatewayException;
import org.smslib.OutboundMessage;
import org.smslib.SMSLibException;
import org.smslib.Service;
import org.smslib.Settings;
import org.smslib.TimeoutException;
import org.smslib.smpp.jsmpp.JSMPPGateway;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ManagedSmslibServiceTest {
    @Mock
    private Service smslibService;
    @Mock
    private OutboundEventGateway outboundEventGateway;

    private Properties smppProperties;
    private Properties smsProperties;
    private SettingsFacade settings;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        mockServiceSettings();
        smppProperties = new Properties() {{
            setProperty(SmppProperties.HOST, "smppserver.com");
            setProperty(SmppProperties.PASSWORD, "wpsd");
            setProperty(SmppProperties.PORT, "8876");
            setProperty(SmppProperties.SYSTEM_ID, "pavel");
            setProperty(SmppProperties.DELIVERY_REPORTS, "true");
            setProperty(SmppProperties.BINDTYPE, "TRANSMITTER");
        }};
        smsProperties = new Properties();

        settings = new SettingsFacade();
        settings.addConfigProperties("smpp.properties", smppProperties);
        settings.addConfigProperties("sms.properties", smsProperties);
    }

    private void mockServiceSettings() throws Exception {
        Constructor constructor = Settings.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        when(smslibService.getSettings()).thenReturn((Settings) constructor.newInstance());
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
    public void shouldRegisterOutboundNotificationListener() {
        OutboundMessageNotification outboundMessageNotification = mock(OutboundMessageNotification.class);
        new ManagedSmslibService(smslibService, outboundMessageNotification, null, settings);
        verify(smslibService).setOutboundMessageNotification(outboundMessageNotification);
    }

    @Test
    public void shouldRegisterInboundMessagesListener() {
        InboundMessageNotification inboundMessageNotification = mock(InboundMessageNotification.class);
        new ManagedSmslibService(smslibService, null, inboundMessageNotification, settings);
        verify(smslibService).setInboundMessageNotification(inboundMessageNotification);
    }

    @Test
    public void shouldAddConfiguredJsmppGatewayDuringInitialization() throws GatewayException {
        new ManagedSmslibService(smslibService, null, null, settings);

        ArgumentCaptor<JSMPPGateway> jsmppGatewayCaptor = ArgumentCaptor.forClass(JSMPPGateway.class);
        verify(smslibService).addGateway(jsmppGatewayCaptor.capture());

        JSMPPGateway gateway = jsmppGatewayCaptor.getValue();
        assertEquals("smppserver.com", gateway.getHost());
        assertEquals(8876, gateway.getPort());
        assertEquals("pavel", gateway.getBindAttributes().getSystemId());
        assertEquals("wpsd", gateway.getBindAttributes().getPassword());
    }

    @Test
    public void shouldConfigureRetryCountOnSmsLib() {
        Service actualSmslibService = Service.getInstance();
        settings.setProperty("sms.properties", SmsProperties.MAX_RETRIES, "5");

        new ManagedSmslibService(actualSmslibService, null, null, settings);
        assertEquals(5, actualSmslibService.getSettings().QUEUE_RETRIES);
    }

    @Test
    public void shouldConfigurePersistenceFilePathOnSmsLib() {
        Service actualSmslibService = Service.getInstance();
        new ManagedSmslibService(actualSmslibService, null, null, settings);
        assertEquals(".", actualSmslibService.getSettings().QUEUE_DIRECTORY);
    }

    @Test
    public void shouldEstablishSmppConnection() throws SMSLibException, IOException, InterruptedException {
        ManagedSmslibService managedSmslibService = new ManagedSmslibService(smslibService, null, null, settings);
        managedSmslibService.connect();
        verify(smslibService).startService();
    }

    @Test
    public void shouldTerminateSmppConnection() throws IOException, SMSLibException, InterruptedException {
        ManagedSmslibService managedSmslibService = new ManagedSmslibService(smslibService, null, null, settings);
        managedSmslibService.disconnect();
        verify(smslibService).stopService();
    }

    @Test
    public void shouldScheduledSmsForDelivery() throws GatewayException, IOException, TimeoutException, InterruptedException {
        ManagedSmslibService managedSmslibService = new ManagedSmslibService(smslibService, null, null, settings);
        List<String> recipients = Arrays.asList("recipient1", "recipient2");
        managedSmslibService.queueMessageAt(recipients, "message", new DateTime(2011, 11, 21, 13, 12, 0, 0));

        ArgumentCaptor<OutboundMessage> outboundMessageCaptor = ArgumentCaptor.forClass(OutboundMessage.class);
        ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);
        verify(smslibService, times(recipients.size())).queueMessageAt(outboundMessageCaptor.capture(), dateCaptor.capture());

        assertEquals("message", outboundMessageCaptor.getValue().getText());
        assertEquals(new DateTime(2011, 11, 21, 13, 12, 0, 0).toDate(), dateCaptor.getValue());
    }

    @Test
    public void shouldNotScheduleSms() throws GatewayException, IOException, TimeoutException, InterruptedException {
        ManagedSmslibService managedSmslibService = new ManagedSmslibService(smslibService, null, null, settings);
        List<String> recipients = Arrays.asList("recipient1", "recipient2");
        managedSmslibService.queueMessage(recipients, "message");

        ArgumentCaptor<OutboundMessage> outboundMessageCaptor = ArgumentCaptor.forClass(OutboundMessage.class);
        verify(smslibService, times(recipients.size())).queueMessage(outboundMessageCaptor.capture());

        assertEquals("message", outboundMessageCaptor.getValue().getText());
    }
}
