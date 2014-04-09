package org.motechproject.tasks.service.impl;

import org.eclipse.gemini.blueprint.mock.MockBundle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.api.BundleIcon;
import org.motechproject.tasks.contract.ActionEventRequest;
import org.motechproject.tasks.contract.ActionParameterRequest;
import org.motechproject.tasks.contract.ChannelRequest;
import org.motechproject.tasks.contract.EventParameterRequest;
import org.motechproject.tasks.contract.TriggerEventRequest;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.ChannelDeregisterEvent;
import org.motechproject.tasks.domain.ChannelRegisterEvent;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.repository.AllChannels;
import org.motechproject.tasks.repository.AllTasks;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TaskService;
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
import java.util.Hashtable;
import java.util.List;
import java.util.TreeSet;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.events.constants.EventDataKeys.CHANNEL_MODULE_NAME;
import static org.motechproject.tasks.events.constants.EventSubjects.CHANNEL_UPDATE_SUBJECT;

public class ChannelServiceImplTest {
    private static final String BUNDLE_SYMBOLIC_NAME = "test";
    private static final String VERSION = "0.16";
    private static final String DEFAULT_ICON = "/webapp/img/iconTaskChannel.png";
    private static final String DEFAULT_ICON_PATH = "/bundle_icon.png";
    private static final String IMAGE_PNG = "image/png";

    @Mock
    AllChannels allChannels;

    @Mock
    private AllTasks allTasks;

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

    @Mock
    private TaskService taskService;

    @Mock
    private IconLoader iconLoader;

    ChannelService channelService;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        channelService = new ChannelServiceImpl(allChannels, resourceLoader, eventRelay, iconLoader);
    }

    @Test(expected = ValidationException.class)
    public void shouldNotRegisterChannelWhenValidationExceptionIsAppeared() {
        String channel = String.format("{displayName: %s, moduleName: %s, moduleVersion: %s}", BUNDLE_SYMBOLIC_NAME, BUNDLE_SYMBOLIC_NAME, VERSION);
        InputStream stream = new ByteArrayInputStream(channel.getBytes(Charset.forName("UTF-8")));

        channelService.registerChannel(stream, null, null);
    }

    @Test
    public void shouldRegisterChannel() {
        String triggerEvent = "{ displayName: 'displayName', subject: 'subject', eventParameters: [{ displayName: 'displayName', eventKey: 'eventKey' }] }";
        String channel = String.format("{displayName: %s, triggerTaskEvents: [%s]}", BUNDLE_SYMBOLIC_NAME, triggerEvent);
        InputStream stream = new ByteArrayInputStream(channel.getBytes(Charset.forName("UTF-8")));

        channelService.registerChannel(stream, BUNDLE_SYMBOLIC_NAME, VERSION);

        ArgumentCaptor<Channel> captor = ArgumentCaptor.forClass(Channel.class);
        verify(allChannels).addOrUpdate(captor.capture());

        Channel c = captor.getValue();

        assertEquals(BUNDLE_SYMBOLIC_NAME, c.getDisplayName());
        assertEquals(BUNDLE_SYMBOLIC_NAME, c.getModuleName());
        assertEquals(VERSION, c.getModuleVersion());
        assertEquals(1, c.getTriggerTaskEvents().size());
        assertEquals(new TriggerEvent("displayName", "subject", null, asList(new EventParameter("displayName", "eventKey"))), c.getTriggerTaskEvents().get(0));
    }

    @Test
    public void shouldRegisterChannelFromChannelRequest() {
        List<ActionEventRequest> actionEventRequests = asList(new ActionEventRequest("actionName", "subject.foo", "action description", "some.interface", "method", new TreeSet<ActionParameterRequest>()));
        List<TriggerEventRequest> triggerEventsRequest = asList(new TriggerEventRequest("displayName", "subject.foo", "description", asList(new EventParameterRequest("displayName", "eventKey"))));
        ChannelRequest channelRequest = new ChannelRequest(BUNDLE_SYMBOLIC_NAME, BUNDLE_SYMBOLIC_NAME, VERSION, "", triggerEventsRequest, actionEventRequests);
        channelService.registerChannel(channelRequest);

        ArgumentCaptor<Channel> captor = ArgumentCaptor.forClass(Channel.class);
        verify(allChannels).addOrUpdate(captor.capture());

        Channel channelToBeCreated = captor.getValue();

        assertEquals(BUNDLE_SYMBOLIC_NAME, channelToBeCreated.getDisplayName());
        assertEquals(BUNDLE_SYMBOLIC_NAME, channelToBeCreated.getModuleName());
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
    public void shouldRemoveChannelOnDeregister() {
        Channel existingChannel = new Channel("module", "moduleName", "1");
        when(allChannels.byModuleName("moduleName")).thenReturn(existingChannel);

        channelService.deregisterChannel("moduleName");
        ArgumentCaptor<Channel> captor = ArgumentCaptor.forClass(Channel.class);
        verify(allChannels).remove(captor.capture());

        Channel channel = captor.getValue();
        assertEquals("moduleName", channel.getModuleName());
    }

    @Test
    public void shouldRaiseEventWhenChannelIsRegistered() {
        List<TriggerEventRequest> triggerEventsRequest = asList(new TriggerEventRequest("displayName", "subject.foo", "description", asList(new EventParameterRequest("displayName", "eventKey"))));
        List<ActionEventRequest> actionEventRequests = asList(new ActionEventRequest("actionName", "subject.foo", "action description", "some.interface", "method", new TreeSet<ActionParameterRequest>()));
        channelService.registerChannel(new ChannelRequest("foo", "moduleName", "0.1", "", triggerEventsRequest, actionEventRequests));

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captor.capture());

        ChannelRegisterEvent event = new ChannelRegisterEvent(captor.getValue());
        assertEquals("moduleName", event.getChannelModuleName());
    }

    @Test
    public void shouldRaiseEventWhenChannelIsDeregistered() {
        Channel existingChannel = new Channel("module", "moduleName", "1");
        when(allChannels.byModuleName("moduleName")).thenReturn(existingChannel);

        channelService.deregisterChannel("moduleName");

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(captor.capture());

        ChannelDeregisterEvent event = new ChannelDeregisterEvent(captor.getValue());
        assertEquals("moduleName", event.getChannelModuleName());
    }

    @Test
    public void shouldSendEventWhenChannelWasUpdated() {
        EventParameter eventParameter = new EventParameter("displayName", "eventKey");
        TriggerEvent triggerEvent = new TriggerEvent("displayName", "subject", null, Arrays.asList(eventParameter));

        Channel channel = new Channel("displayName", BUNDLE_SYMBOLIC_NAME, VERSION);
        channel.getTriggerTaskEvents().add(triggerEvent);

        when(allChannels.addOrUpdate(channel)).thenReturn(true);

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);
        channelService.addOrUpdate(channel);

        verify(allChannels).addOrUpdate(channel);
        verify(eventRelay).sendEventMessage(captor.capture());

        MotechEvent event = captor.getValue();

        assertEquals(CHANNEL_UPDATE_SUBJECT, event.getSubject());
        assertEquals(BUNDLE_SYMBOLIC_NAME, event.getParameters().get(CHANNEL_MODULE_NAME));
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

        channelService.getChannelIcon(BUNDLE_SYMBOLIC_NAME);
    }

    @Test
    public void shouldReturnDefaultIconWhenBundleNotFound() throws IOException {
        ((ChannelServiceImpl) channelService).setBundleContext(bundleContext);
        byte[] image = new byte[]{0, 1, 0, 1};

        Bundle[] bundles = {new MockBundle("some-other-bundle")};
        when(bundleContext.getBundles()).thenReturn(bundles);

        ClassPathResource defaultIconResource = new ClassPathResource(DEFAULT_ICON_PATH);
        when(resourceLoader.getResource(DEFAULT_ICON)).thenReturn(defaultIconResource);

        BundleIcon expectedBundleIcon = new BundleIcon(image, IMAGE_PNG);
        when(iconLoader.load(defaultIconResource.getURL())).thenReturn(expectedBundleIcon);

        BundleIcon bundleIcon = channelService.getChannelIcon(BUNDLE_SYMBOLIC_NAME);

        assertNotNull(bundleIcon);
        assertEquals(IMAGE_PNG, bundleIcon.getMime());
        assertArrayEquals(image, bundleIcon.getIcon());
        assertEquals(image.length, bundleIcon.getContentLength());

        bundleIcon = channelService.getChannelIcon("faceModule");

        assertNotNull(bundleIcon);
        assertEquals(IMAGE_PNG, bundleIcon.getMime());
        assertArrayEquals(image, bundleIcon.getIcon());
        assertEquals(image.length, bundleIcon.getContentLength());
    }

    @Test
    public void shouldReturnDefaultIconWhenBundleDoesNotContainIcon() throws IOException {
        ((ChannelServiceImpl) channelService).setBundleContext(bundleContext);
        byte[] image = new byte[]{1, 1, 1, 1};


        Bundle bundle = mock(Bundle.class);
        when(bundle.getHeaders()).thenReturn(new Hashtable<String, String>());
        when(bundle.getSymbolicName()).thenReturn(BUNDLE_SYMBOLIC_NAME);
        when(bundle.getResource(anyString())).thenReturn(null);
        Bundle[] bundles = {bundle};
        when(bundleContext.getBundles()).thenReturn(bundles);

        ClassPathResource bundleIconResource = new ClassPathResource(DEFAULT_ICON_PATH);
        when(resourceLoader.getResource(DEFAULT_ICON)).thenReturn(bundleIconResource);
        when(iconLoader.load(bundleIconResource.getURL())).thenReturn(new BundleIcon(image, IMAGE_PNG));
        

        BundleIcon bundleIcon = channelService.getChannelIcon(BUNDLE_SYMBOLIC_NAME);

        assertNotNull(bundleIcon);
        assertEquals(IMAGE_PNG, bundleIcon.getMime());
        assertArrayEquals(image, bundleIcon.getIcon());
        assertEquals(image.length, bundleIcon.getContentLength());
    }

    @Test
    public void shouldGetChannelIconForBundleSymbolicName() throws IOException {
        ((ChannelServiceImpl) channelService).setBundleContext(bundleContext);


        String symbolicName = "org.motechproject.motech-mobileforms-api-bundle";
        Bundle mobileFormsBundle = mock(Bundle.class);
        when(mobileFormsBundle.getSymbolicName()).thenReturn(symbolicName);
        URL iconUrl = new URL("http://some.url");
        when(mobileFormsBundle.getHeaders()).thenReturn(new Hashtable<String, String>());
        when(mobileFormsBundle.getResource(anyString())).thenReturn(iconUrl);

        Bundle fooBundle = mock(Bundle.class);
        when(fooBundle.getHeaders()).thenReturn(new Hashtable<String, String>());
        when(fooBundle.getSymbolicName()).thenReturn("org.motechproject.motech-foo-bundle");

        BundleIcon expectedIcon = mock(BundleIcon.class);
        when(iconLoader.load(iconUrl)).thenReturn(expectedIcon);


        Bundle[] bundles = {mobileFormsBundle, fooBundle};
        when(bundleContext.getBundles()).thenReturn(bundles);

        BundleIcon actualIcon = channelService.getChannelIcon(symbolicName);

        assertEquals(expectedIcon, actualIcon);
    }

    @Test
    public void shouldClearAllChannels() {
        List<Channel> channels = asList(new Channel("foo", "moduleName", "0.1"), new Channel("bar", "anotherModuleName", "0.1"));
        when(channelService.getAllChannels()).thenReturn(channels);

        channelService.deregisterAllChannels();

        ArgumentCaptor<Channel> channelCaptor = ArgumentCaptor.forClass(Channel.class);
        verify(allChannels, times(2)).remove(channelCaptor.capture());

        List<String> deregisteredChannelNames = extract(channelCaptor.getAllValues(), on(Channel.class).getModuleName());
        assertEquals(2, deregisteredChannelNames.size());
        assertTrue(deregisteredChannelNames.contains("moduleName"));
        assertTrue(deregisteredChannelNames.contains("anotherModuleName"));
    }

    private void whenGetChannelIcon(URL iconUrl) throws IOException {
        when(bundle.getSymbolicName()).thenReturn(BUNDLE_SYMBOLIC_NAME);
        when(bundle.getVersion()).thenReturn(Version.parseVersion(VERSION));
        when(bundle.getResource(BundleIcon.ICON_LOCATIONS[0])).thenReturn(iconUrl);

        when(bundleContext.getBundles()).thenReturn(new Bundle[]{bundle});
    }

    private static URL getDefaultIconUrl() {
        return ChannelServiceImplTest.class.getResource(DEFAULT_ICON_PATH);
    }
}
