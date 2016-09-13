package org.motechproject.tasks.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.tasks.contract.builder.TestActionEventRequestBuilder;
import org.motechproject.tasks.contract.ActionEventRequest;
import org.motechproject.tasks.contract.ActionParameterRequest;
import org.motechproject.tasks.contract.ChannelRequest;
import org.motechproject.tasks.contract.EventParameterRequest;
import org.motechproject.tasks.contract.TriggerEventRequest;
import org.motechproject.tasks.domain.mds.channel.ActionEvent;
import org.motechproject.tasks.domain.mds.channel.Channel;
import org.motechproject.tasks.domain.mds.channel.EventParameter;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;
import org.motechproject.tasks.domain.mds.channel.builder.ActionEventBuilder;
import org.motechproject.tasks.exception.ValidationException;
import org.motechproject.tasks.repository.ChannelsDataService;
import org.motechproject.tasks.repository.TasksDataService;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.tasks.service.TriggerEventService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.constants.EventDataKeys.CHANNEL_MODULE_NAME;
import static org.motechproject.tasks.constants.EventSubjects.CHANNEL_DEREGISTER_SUBJECT;
import static org.motechproject.tasks.constants.EventSubjects.CHANNEL_UPDATE_SUBJECT;

public class ChannelServiceImplTest {
    private static final String BUNDLE_SYMBOLIC_NAME = "test";
    private static final String VERSION = "0.16";

    @Mock
    ChannelsDataService channelsDataService;

    @Mock
    private TasksDataService tasksDataService;

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
    private ApplicationContext applicationContext;

    @Mock
    private TriggerEventService triggerEventService;

    private ChannelService channelService;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        channelService = new ChannelServiceImpl(triggerEventService, channelsDataService, eventRelay);
        ((ChannelServiceImpl) channelService).setBundleContext(bundleContext);
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
        verify(channelsDataService).create(captor.capture());

        Channel c = captor.getValue();

        assertEquals(BUNDLE_SYMBOLIC_NAME, c.getDisplayName());
        assertEquals(BUNDLE_SYMBOLIC_NAME, c.getModuleName());
        assertEquals(VERSION, c.getModuleVersion());
        assertEquals(1, c.getTriggerTaskEvents().size());
        assertEquals(new TriggerEvent("displayName", "subject", null, asList(new EventParameter("displayName", "eventKey")), ""), c.getTriggerTaskEvents().get(0));
    }

    @Test
    public void shouldRegisterChannelFromChannelRequest() {
        List<ActionEventRequest> actionEventRequests = asList(new TestActionEventRequestBuilder().setDisplayName("actionName")
                .setSubject("subject.foo").setDescription("action description").setServiceInterface("some.interface")
                .setServiceMethod("method").setActionParameters(new TreeSet<ActionParameterRequest>())
                .setPostActionParameters(new TreeSet<ActionParameterRequest>()).createActionEventRequest());
        List<TriggerEventRequest> triggerEventsRequest = asList(new TriggerEventRequest("displayName", "subject.foo", "description", asList(new EventParameterRequest("displayName", "eventKey"))));
        ChannelRequest channelRequest = new ChannelRequest(BUNDLE_SYMBOLIC_NAME, BUNDLE_SYMBOLIC_NAME, VERSION, "", triggerEventsRequest, actionEventRequests);

        channelService.registerChannel(channelRequest);

        ArgumentCaptor<Channel> captor = ArgumentCaptor.forClass(Channel.class);
        verify(channelsDataService).create(captor.capture());

        Channel channelToBeCreated = captor.getValue();

        assertEquals(BUNDLE_SYMBOLIC_NAME, channelToBeCreated.getDisplayName());
        assertEquals(BUNDLE_SYMBOLIC_NAME, channelToBeCreated.getModuleName());
        assertEquals(VERSION, channelToBeCreated.getModuleVersion());

        assertEquals(1, channelToBeCreated.getTriggerTaskEvents().size());
        TriggerEvent expectedTrigger = new TriggerEvent("displayName", "subject.foo", "description", asList(new EventParameter("displayName", "eventKey")), "");
        TriggerEvent actualTrigger = channelToBeCreated.getTriggerTaskEvents().get(0);
        assertEquals(expectedTrigger, actualTrigger);

        assertEquals(1, channelToBeCreated.getActionTaskEvents().size());
        ActionEvent expectedAction = new ActionEventBuilder().setDisplayName("actionName").setSubject("subject.foo")
                .setDescription("action description").setServiceInterface("some.interface").setServiceMethod("method")
                .setActionParameters(new TreeSet<>()).setPostActionParameters(new TreeSet<>()).build();
        ActionEvent actualAction = channelToBeCreated.getActionTaskEvents().get(0);
        assertEquals(expectedAction, actualAction);
    }

    @Test
    public void shouldUnregisterChannel() {
        Channel channel = new Channel("Channel to delete", BUNDLE_SYMBOLIC_NAME, VERSION);

        when(channelsDataService.findByModuleName(channel.getModuleName())).thenReturn(channel);
        when(bundleContext.getBundles()).thenReturn(new Bundle[]{bundle});
        when(bundle.getSymbolicName()).thenReturn(BUNDLE_SYMBOLIC_NAME);

        channelService.unregisterChannel(channel.getModuleName());

        ArgumentCaptor<Channel> captor = ArgumentCaptor.forClass(Channel.class);
        verify(channelsDataService).delete(captor.capture());

        Channel deletedChannel = captor.getValue();

        assertEquals("Channel to delete", deletedChannel.getDisplayName());
        assertEquals(BUNDLE_SYMBOLIC_NAME, deletedChannel.getModuleName());
        assertEquals(VERSION, deletedChannel.getModuleVersion());
    }

    @Test
    public void shouldSendEventWhenChannelWasDeleted() {
        Channel channel = new Channel("displayName", BUNDLE_SYMBOLIC_NAME, VERSION);

        when(channelsDataService.findByModuleName(channel.getModuleName())).thenReturn(channel);
        when(bundleContext.getBundles()).thenReturn(new Bundle[]{bundle});
        when(bundle.getSymbolicName()).thenReturn(BUNDLE_SYMBOLIC_NAME);

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);
        Channel deletedChannel = new Channel("displayName2", BUNDLE_SYMBOLIC_NAME, VERSION);
        channelService.delete(deletedChannel.getModuleName());

        verify(channelsDataService).delete(channel);

        verify(eventRelay).sendEventMessage(captor.capture());

        MotechEvent event = captor.getValue();

        assertEquals(CHANNEL_DEREGISTER_SUBJECT, event.getSubject());
        assertEquals(BUNDLE_SYMBOLIC_NAME, event.getParameters().get(CHANNEL_MODULE_NAME));
    }

    @Test
    public void shouldSendEventWhenChannelWasUpdated() {
        Channel channel = new Channel("displayName", BUNDLE_SYMBOLIC_NAME, VERSION);

        EventParameter eventParameter = new EventParameter("displayName", "eventKey");
        TriggerEvent triggerEvent = new TriggerEvent("displayName", "subject", null, Arrays.asList(eventParameter), "");

        channel.getTriggerTaskEvents().add(triggerEvent);

        when(channelsDataService.findByModuleName(channel.getModuleName())).thenReturn(channel);

        when(bundleContext.getBundles()).thenReturn(new Bundle[]{bundle});
        when(bundle.getSymbolicName()).thenReturn(BUNDLE_SYMBOLIC_NAME);

        ArgumentCaptor<MotechEvent> captor = ArgumentCaptor.forClass(MotechEvent.class);
        Channel updatedChannel = new Channel("displayName2", BUNDLE_SYMBOLIC_NAME, VERSION);
        updatedChannel.getTriggerTaskEvents().add(triggerEvent);
        channelService.addOrUpdate(updatedChannel);

        verify(channelsDataService).update(channel);

        verify(eventRelay).sendEventMessage(captor.capture());

        MotechEvent event = captor.getValue();

        assertEquals(CHANNEL_UPDATE_SUBJECT, event.getSubject());
        assertEquals(BUNDLE_SYMBOLIC_NAME, event.getParameters().get(CHANNEL_MODULE_NAME));
    }

    @Test
    public void shouldGetAllChannels() {
        List<Channel> expected = new ArrayList<>();
        expected.add(new Channel(null, "symbolic", null));
        expected.add(new Channel(null, "symbolic", null));

        when(bundleContext.getBundles()).thenReturn(new Bundle[]{bundle});
        when(bundle.getSymbolicName()).thenReturn("symbolic");

        when(channelsDataService.executeQuery(any(QueryExecution.class))).thenReturn(expected);

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

        when(channelsDataService.findByModuleName(moduleName)).thenReturn(expected);

        when(bundleContext.getBundles()).thenReturn(new Bundle[]{bundle});
        when(bundle.getSymbolicName()).thenReturn(moduleName);

        Channel actual = channelService.getChannel(moduleName);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
