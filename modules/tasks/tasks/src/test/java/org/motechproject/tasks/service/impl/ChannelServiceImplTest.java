package org.motechproject.tasks.service.impl;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.api.BundleIcon;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.repository.AllChannels;
import org.motechproject.tasks.contract.ActionEventRequest;
import org.motechproject.tasks.contract.ActionParameterRequest;
import org.motechproject.tasks.contract.ChannelRequest;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.contract.EventParameterRequest;
import org.motechproject.tasks.contract.TriggerEventRequest;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.events.constants.EventDataKeys.CHANNEL_MODULE_NAME;
import static org.motechproject.tasks.events.constants.EventSubjects.CHANNEL_UPDATE_SUBJECT;

public class ChannelServiceImplTest {
    private static final String MODULE_NAME = "test";
    private static final String VERSION = "0.16";
    private static final String DEFAULT_ICON = "/webapp/img/iconTaskChannel.png";
    private static final String DEFAULT_ICON_PATH = "/bundle_icon.png";

    @Mock
    AllChannels allChannels;

    @Mock
    EventRelay eventRelay;

    @Mock
    BundleContext bundleContext;

    @Mock
    ResourceLoader resourceLoader;

    @Mock
    Bundle bundle;

    @Mock
    InputStream inputStream;

    ChannelService channelService;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        channelService = new ChannelServiceImpl(allChannels, resourceLoader, eventRelay);
    }

    @Test(expected = ValidationException.class)
    public void shouldNotRegisterChannelWhenValidationExceptionIsAppeared() {
        String channel = String.format("{displayName: %s, moduleName: %s, moduleVersion: %s}", MODULE_NAME, MODULE_NAME, VERSION);
        InputStream stream = new ByteArrayInputStream(channel.getBytes(Charset.forName("UTF-8")));

        channelService.registerChannel(stream);
    }

    @Test
    public void shouldRegisterChannel() {
        String triggerEvent = "{ displayName: 'displayName', subject: 'subject', eventParameters: [{ displayName: 'displayName', eventKey: 'eventKey' }] }";
        String channel = String.format("{displayName: %s, moduleName: %s, moduleVersion: %s, triggerTaskEvents: [%s]}", MODULE_NAME, MODULE_NAME, VERSION, triggerEvent);
        InputStream stream = new ByteArrayInputStream(channel.getBytes(Charset.forName("UTF-8")));

        channelService.registerChannel(stream);

        ArgumentCaptor<Channel> captor = ArgumentCaptor.forClass(Channel.class);
        verify(allChannels).addOrUpdate(captor.capture());

        Channel c = captor.getValue();

        assertEquals(MODULE_NAME, c.getDisplayName());
        assertEquals(MODULE_NAME, c.getModuleName());
        assertEquals(VERSION, c.getModuleVersion());
        assertEquals(1, c.getTriggerTaskEvents().size());
        assertEquals(new TriggerEvent("displayName", "subject", null, asList(new EventParameter("displayName", "eventKey"))), c.getTriggerTaskEvents().get(0));
    }

    @Test
    public void shouldRegisterChannelFromChannelRequest() {
        List<ActionEventRequest> actionEventRequests = asList(new ActionEventRequest("actionName", "subject.foo", "action description", "some.interface", "method", new TreeSet<ActionParameterRequest>()));
        List<TriggerEventRequest> triggerEventsRequest = asList(new TriggerEventRequest("displayName", "subject.foo", "description", asList(new EventParameterRequest("displayName", "eventKey"))));
        ChannelRequest channelRequest = new ChannelRequest(MODULE_NAME, MODULE_NAME, VERSION, "", triggerEventsRequest, actionEventRequests);
        channelService.registerChannel(channelRequest);

        ArgumentCaptor<Channel> captor = ArgumentCaptor.forClass(Channel.class);
        verify(allChannels).addOrUpdate(captor.capture());

        Channel channelToBeCreated = captor.getValue();

        assertEquals(MODULE_NAME, channelToBeCreated.getDisplayName());
        assertEquals(MODULE_NAME, channelToBeCreated.getModuleName());
        assertEquals(VERSION, channelToBeCreated.getModuleVersion());

        assertEquals(1, channelToBeCreated.getTriggerTaskEvents().size());
        TriggerEvent expectedTrigger = new TriggerEvent("displayName", "subject.foo", "description", asList(new EventParameter("displayName", "eventKey")));
        TriggerEvent actualTrigger = channelToBeCreated.getTriggerTaskEvents().get(0);
        assertEquals(expectedTrigger, actualTrigger);

        assertEquals(1, channelToBeCreated.getActionTaskEvents().size());
        ActionEvent expectedAction = new ActionEvent("actionName", "subject.foo", "action description", "some.interface", "method", new TreeSet<ActionParameter>());
        ActionEvent actualAction = channelToBeCreated.getActionTaskEvents().get(0);
        assertEquals(expectedAction, actualAction);
    }

    @Test
    public void shouldSendEventWhenChannelWasUpdated() {
        EventParameter eventParameter = new EventParameter("displayName", "eventKey");
        TriggerEvent triggerEvent = new TriggerEvent("displayName", "subject", null, Arrays.asList(eventParameter));

        Channel channel = new Channel("displayName", MODULE_NAME, VERSION);
        channel.getTriggerTaskEvents().add(triggerEvent);

        when(allChannels.addOrUpdate(channel)).thenReturn(true);

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);
        channelService.addOrUpdate(channel);

        verify(allChannels).addOrUpdate(channel);
        verify(eventRelay).sendEventMessage(captor.capture());

        MotechEvent event = captor.getValue();

        assertEquals(CHANNEL_UPDATE_SUBJECT, event.getSubject());
        assertEquals(MODULE_NAME, event.getParameters().get(CHANNEL_MODULE_NAME));
    }

    @Test
    public void shouldGetAllChannels() {
        List<Channel> expected = new ArrayList<>();
        expected.add(new Channel());
        expected.add(new Channel());

        when(allChannels.getAll()).thenReturn(expected);

        List<Channel> actual = channelService.getAllChannels();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetChannelByChannelInfo() {
        String displayName = "Test";
        String moduleName = "test-1";

        Channel expected = new Channel();
        expected.setDisplayName(displayName);
        expected.setModuleName(moduleName);
        expected.setModuleVersion(VERSION);

        when(allChannels.byModuleName(moduleName)).thenReturn(expected);

        Channel actual = channelService.getChannel(moduleName);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenBundleContextNotSet() throws IOException {
        whenGetChannelIcon(getDefaultIconUrl());

        channelService.getChannelIcon(MODULE_NAME);
    }

    @Test
    public void shouldReturnDefaultIconWhenBundleNotFound() throws IOException {
        ((ChannelServiceImpl) channelService).setBundleContext(bundleContext);
        byte[] image = readDefaultIcon();

        when(resourceLoader.getResource(DEFAULT_ICON)).thenReturn(new ClassPathResource(DEFAULT_ICON_PATH));
        whenGetChannelIcon(getDefaultIconUrl());

        BundleIcon bundleIcon = channelService.getChannelIcon(MODULE_NAME);

        assertNotNull(bundleIcon);
        assertEquals("image/png", bundleIcon.getMime());
        assertArrayEquals(image, bundleIcon.getIcon());
        assertEquals(image.length, bundleIcon.getContentLength());

        bundleIcon = channelService.getChannelIcon("faceModule");

        assertNotNull(bundleIcon);
        assertEquals("image/png", bundleIcon.getMime());
        assertArrayEquals(image, bundleIcon.getIcon());
        assertEquals(image.length, bundleIcon.getContentLength());
    }

    @Test
    public void shouldReturnDefaultIconWhenBundleNotContainIcon() throws IOException {
        ((ChannelServiceImpl) channelService).setBundleContext(bundleContext);
        byte[] image = readDefaultIcon();

        when(resourceLoader.getResource(DEFAULT_ICON)).thenReturn(new ClassPathResource(DEFAULT_ICON_PATH));
        whenGetChannelIcon(null);

        BundleIcon bundleIcon = channelService.getChannelIcon(MODULE_NAME);

        assertNotNull(bundleIcon);
        assertEquals("image/png", bundleIcon.getMime());
        assertArrayEquals(image, bundleIcon.getIcon());
        assertEquals(image.length, bundleIcon.getContentLength());
    }

    @Test
    public void shouldGetChannelIcon() throws IOException {
        ((ChannelServiceImpl) channelService).setBundleContext(bundleContext);
        byte[] image = readDefaultIcon();

        whenGetChannelIcon(getDefaultIconUrl());

        BundleIcon bundleIcon = channelService.getChannelIcon(MODULE_NAME);

        assertNotNull(bundleIcon);
        assertEquals("image/png", bundleIcon.getMime());
        assertArrayEquals(image, bundleIcon.getIcon());
        assertEquals(image.length, bundleIcon.getContentLength());
    }


    private void whenGetChannelIcon(URL iconUrl) throws IOException {
        when(bundle.getSymbolicName()).thenReturn(String.format("org.motechproject.%s", MODULE_NAME));
        when(bundle.getVersion()).thenReturn(Version.parseVersion(VERSION));
        when(bundle.getResource(BundleIcon.ICON_LOCATIONS[0])).thenReturn(iconUrl);

        when(bundleContext.getBundles()).thenReturn(new Bundle[]{bundle});
    }

    private static byte[] readDefaultIcon() {
        URL url = getDefaultIconUrl();

        try (InputStream is = url.openStream()) {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static URL getDefaultIconUrl() {
        return ChannelServiceImplTest.class.getResource(DEFAULT_ICON_PATH);
    }
}
