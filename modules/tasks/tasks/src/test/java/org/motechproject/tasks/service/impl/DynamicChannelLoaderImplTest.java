package org.motechproject.tasks.service.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tasks.service.DynamicChannelProvider;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;
import org.motechproject.tasks.service.DynamicChannelLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DynamicChannelLoaderImplTest {

    private static final String MODULE_SYMBOLIC_NAME = "test_bundle";
    private static final String SUBJECT_ONE_DISPLAY_NAME = "subjectOne";
    private static final String SUBJECT_ONE = "subject.one";
    private static final String DESCRIPTION = "description";

    @Mock
    private DynamicChannelProvider provider;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceReference reference;

    @Mock
    private Bundle bundle;

    private DynamicChannelLoader channelLoader;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        channelLoader = new DynamicChannelLoaderImpl(bundleContext);
        ServiceReference[] references = new ServiceReference[1];
        references[0] = reference;

        when(bundle.getSymbolicName()).thenReturn(MODULE_SYMBOLIC_NAME);
        when(reference.getBundle()).thenReturn(bundle);
        when(bundleContext.getService(reference)).thenReturn(provider);
        when(bundleContext.getServiceReferences(eq(DynamicChannelProvider.class.getName()), eq(null)))
                .thenReturn(references);
    }

    @Test
    public void testGetDynamicTriggers() throws Exception {

        List<TriggerEvent> triggerEvents = prepareTriggerEvents();

        when(provider.getTriggers(1, 10)).thenReturn(triggerEvents);

        List<TriggerEvent> fetched = channelLoader.getDynamicTriggers(MODULE_SYMBOLIC_NAME, 1, 10);

        assertEquals(triggerEvents, fetched);

        verify(provider).getTriggers(1, 10);
    }

    @Test
    public void testProvidesDynamicTriggers() throws Exception {

        assertTrue(channelLoader.providesDynamicTriggers(MODULE_SYMBOLIC_NAME));

    }

    @Test
    public void testCountByChannelModuleName() throws Exception {

        Long expectedCount = Long.valueOf(404);

        when(provider.countTriggers()).thenReturn(expectedCount);

        Long count = channelLoader.countByChannelModuleName(MODULE_SYMBOLIC_NAME);

        assertEquals(expectedCount, count);

        verify(provider).countTriggers();
    }

    @Test
    public void testGetTrigger() throws Exception {

        TaskTriggerInformation info = prepareTaskTriggerInformation();
        TriggerEvent triggerEvent = prepareTriggerEvent();

        when(provider.getTrigger(eq(info))).thenReturn(triggerEvent);

        TriggerEvent fetched = channelLoader.getTrigger(info);

        assertEquals(triggerEvent, fetched);

        verify(provider).getTrigger(eq(info));
    }

    @Test
    public void testChannelExists() throws Exception {

        assertTrue(channelLoader.channelExists(MODULE_SYMBOLIC_NAME));

    }

    @Test
    public void testValidateTrigger() throws Exception {

        when(provider.validateSubject(eq(SUBJECT_ONE))).thenReturn(true);

        assertTrue(channelLoader.validateTrigger(MODULE_SYMBOLIC_NAME, SUBJECT_ONE));

        verify(provider).validateSubject(eq(SUBJECT_ONE));
    }

    @After
    public void tearDown() throws Exception {
        verify(bundle).getSymbolicName();
        verify(reference).getBundle();
        verify(bundleContext).getService(eq(reference));
        verify(bundleContext).getServiceReferences(eq(DynamicChannelProvider.class.getName()), eq(null));
    }

    private List<TriggerEvent> prepareTriggerEvents() {
        List<TriggerEvent> triggers = new ArrayList<>();
        triggers.add(prepareTriggerEvent());
        triggers.add(new TriggerEvent("subjectTwo", "subject.two", DESCRIPTION, null, null));
        return triggers;
    }

    private TriggerEvent prepareTriggerEvent() {
        return new TriggerEvent(SUBJECT_ONE_DISPLAY_NAME, SUBJECT_ONE, DESCRIPTION, null, null);
    }

    private TaskTriggerInformation prepareTaskTriggerInformation() {
        return new TaskTriggerInformation(SUBJECT_ONE_DISPLAY_NAME, "TestModule", MODULE_SYMBOLIC_NAME, "1.0",
                SUBJECT_ONE, null);
    }
}