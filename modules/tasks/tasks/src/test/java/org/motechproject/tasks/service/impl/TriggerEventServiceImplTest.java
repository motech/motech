package org.motechproject.tasks.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.tasks.domain.mds.task.TaskError;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;
import org.motechproject.tasks.repository.ChannelsDataService;
import org.motechproject.tasks.repository.TriggerEventsDataService;
import org.motechproject.tasks.service.DynamicChannelLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TriggerEventServiceImplTest {

    private static final String MODULE_NAME = "module-name";

    @Mock
    private TriggerEventsDataService triggerEventsDataService;

    @Mock
    private ChannelsDataService channelsDataService;

    @Mock
    private DynamicChannelLoader dynamicChannelLoader;

    private TriggerEventServiceImpl triggerEventService;

    @Before
    public void setUp() {
        initMocks(this);
        triggerEventService = new TriggerEventServiceImpl();
        triggerEventService.setChannelsDataService(channelsDataService);
        triggerEventService.setDynamicChannelLoader(dynamicChannelLoader);
        triggerEventService.setTriggerEventsDataService(triggerEventsDataService);
    }

    @Test
    public void shouldGetDynamicTriggers() throws Exception {

        int page = 1;
        int pageSize = 10;
        List<TriggerEvent> expectedTriggers = prepareTriggers();

        when(dynamicChannelLoader.getDynamicTriggers(MODULE_NAME, page, pageSize)).thenReturn(expectedTriggers);

        List<TriggerEvent> triggers = triggerEventService.getDynamicTriggers(MODULE_NAME, page, pageSize);

        verify(dynamicChannelLoader).getDynamicTriggers(MODULE_NAME, page, pageSize);

        assertEquals(expectedTriggers, triggers);
    }

    @Test
    public void shouldGetStaticTriggers() throws Exception {

        int page = 1;
        int pageSize = 10;
        List<TriggerEvent> expectedTriggers = prepareTriggers();
        QueryParams params = new QueryParams(page, pageSize);

        when(triggerEventsDataService.byChannelModuleName(MODULE_NAME, params)).thenReturn(expectedTriggers);

        List<TriggerEvent> triggers = triggerEventService.getStaticTriggers(MODULE_NAME, page, pageSize);

        verify(triggerEventsDataService, times(1)).byChannelModuleName(eq(MODULE_NAME), eq(params));

        assertEquals(expectedTriggers, triggers);
    }

    @Test
    public void shouldGetTrigger() throws Exception {

        TriggerEvent expectedTrigger = prepareTrigger();
        TaskTriggerInformation triggerInformation = prepareTaskTriggerInformation(expectedTrigger);

        when(triggerEventsDataService.byChannelModuleNameAndSubject(MODULE_NAME,
                expectedTrigger.getSubject())).thenReturn(null);
        when(dynamicChannelLoader.getTrigger(eq(triggerInformation))).thenReturn(expectedTrigger);

        TriggerEvent trigger = triggerEventService.getTrigger(triggerInformation);

        verify(triggerEventsDataService, times(1)).byChannelModuleNameAndSubject(MODULE_NAME,
                expectedTrigger.getSubject());
        verify(dynamicChannelLoader, times(1)).getTrigger(triggerInformation);

        assertEquals(expectedTrigger, trigger);
    }

    @Test
    public void shouldReturnTrueIfTriggerExists() throws Exception {

        TriggerEvent expectedTrigger = prepareTrigger();
        TaskTriggerInformation triggerInformation = prepareTaskTriggerInformation(expectedTrigger);

        when(triggerEventsDataService.byChannelModuleNameAndSubject(MODULE_NAME,
                expectedTrigger.getSubject())).thenReturn(null);
        when(dynamicChannelLoader.getTrigger(eq(triggerInformation))).thenReturn(expectedTrigger);

        boolean exists = triggerEventService.triggerExists(triggerInformation);

        verify(triggerEventsDataService, times(1)).byChannelModuleNameAndSubject(MODULE_NAME,
                expectedTrigger.getSubject());
        verify(dynamicChannelLoader, times(1)).getTrigger(triggerInformation);

        assertTrue(exists);
    }

    @Test
    public void shouldReturnFalseIfTriggerDoesNotExist() throws Exception {

        TaskTriggerInformation triggerInformation = prepareTaskTriggerInformation(prepareTrigger());

        when(triggerEventsDataService.byChannelModuleNameAndSubject(MODULE_NAME,
                triggerInformation.getSubject())).thenReturn(null);
        when(dynamicChannelLoader.getTrigger(eq(triggerInformation))).thenReturn(null);

        boolean exists = triggerEventService.triggerExists(triggerInformation);

        verify(triggerEventsDataService, times(1)).byChannelModuleNameAndSubject(MODULE_NAME,
                triggerInformation.getSubject());
        verify(dynamicChannelLoader, times(1)).getTrigger(triggerInformation);

        assertFalse(exists);
    }

    @Test
    public void shouldReturnTrueIfModuleProvidesDynamicTriggers() throws Exception {

        when(dynamicChannelLoader.providesDynamicTriggers(MODULE_NAME)).thenReturn(true);

        boolean providesTriggers = triggerEventService.providesDynamicTriggers(MODULE_NAME);

        verify(dynamicChannelLoader, times(1)).providesDynamicTriggers(MODULE_NAME);

        assertTrue(providesTriggers);
    }

    @Test
    public void shouldReturnFalseIfModuleDoesNotProvideDynamicTrigger() throws Exception {

        when(dynamicChannelLoader.channelExists(MODULE_NAME)).thenReturn(false);

        boolean providesTriggers = triggerEventService.providesDynamicTriggers(MODULE_NAME);

        verify(dynamicChannelLoader, times(1)).providesDynamicTriggers(MODULE_NAME);

        assertFalse(providesTriggers);
    }


    @Test
    public void shouldCountStaticTriggers() throws Exception {

        long expectedCount = 1992;

        when(triggerEventsDataService.countByChannelModuleName(MODULE_NAME)).thenReturn(expectedCount);

        long count = triggerEventService.countStaticTriggers(MODULE_NAME);

        verify(triggerEventsDataService, times(1)).countByChannelModuleName(MODULE_NAME);

        assertEquals(expectedCount, count);
    }

    @Test
    public void shouldCountDynamicTriggers() throws Exception {

        long expectedCount = 1992;

        when(dynamicChannelLoader.countByChannelModuleName(MODULE_NAME)).thenReturn(expectedCount);

        long count = triggerEventService.countDynamicTriggers(MODULE_NAME);

        verify(dynamicChannelLoader, times(1)).countByChannelModuleName(MODULE_NAME);

        assertEquals(expectedCount, count);
    }

    @Test
    public void shouldValidateStaticTrigger() throws Exception {

        TaskTriggerInformation triggerInformation = prepareTaskTriggerInformation(prepareTrigger());

        when(channelsDataService.countFindByModuleName(MODULE_NAME)).thenReturn((long) 1992);
        when(triggerEventsDataService.countByChannelModuleNameAndSubject(MODULE_NAME,
                triggerInformation.getSubject())).thenReturn((long) 1);

        Set<TaskError> errors = triggerEventService.validateTrigger(triggerInformation);

        verify(channelsDataService, times(1)).countFindByModuleName(MODULE_NAME);
        verify(triggerEventsDataService, times(1)).countByChannelModuleNameAndSubject(MODULE_NAME,
                triggerInformation.getSubject());
        verify(dynamicChannelLoader, never()).channelExists(anyString());
        verify(dynamicChannelLoader, never()).getTrigger(any(TaskTriggerInformation.class));

        assertEquals(0, errors.size());
    }

    @Test
    public void shouldValidateDynamicTrigger() throws Exception {

        TaskTriggerInformation triggerInformation = prepareTaskTriggerInformation(prepareTrigger());

        when(channelsDataService.countFindByModuleName(MODULE_NAME)).thenReturn((long) 1992);
        when(triggerEventsDataService.countByChannelModuleNameAndSubject(MODULE_NAME,
                triggerInformation.getSubject())).thenReturn((long) 0);
        when(dynamicChannelLoader.channelExists(MODULE_NAME)).thenReturn(true);
        when(dynamicChannelLoader.validateTrigger(MODULE_NAME, triggerInformation.getSubject())).thenReturn(true);

        Set<TaskError> errors = triggerEventService.validateTrigger(triggerInformation);

        verify(channelsDataService, times(1)).countFindByModuleName(MODULE_NAME);
        verify(triggerEventsDataService, times(1)).countByChannelModuleNameAndSubject(MODULE_NAME,
                triggerInformation.getSubject());
        verify(dynamicChannelLoader, times(1)).channelExists(MODULE_NAME);
        verify(dynamicChannelLoader, times(1)).validateTrigger(MODULE_NAME, triggerInformation.getSubject());

        assertEquals(0, errors.size());
    }

    @Test
    public void shouldReturnChannelNotFoundErrorIfChannelDoesNotExist() throws Exception {

        TaskTriggerInformation triggerInformation = prepareTaskTriggerInformation(prepareTrigger());

        when(channelsDataService.countFindByModuleName(MODULE_NAME)).thenReturn((long) 0);
        when(dynamicChannelLoader.channelExists(MODULE_NAME)).thenReturn(false);

        Set<TaskError> errors = triggerEventService.validateTrigger(triggerInformation);

        verify(channelsDataService, times(1)).countFindByModuleName(MODULE_NAME);
        verify(triggerEventsDataService, never()).countByChannelModuleNameAndSubject(MODULE_NAME,
                triggerInformation.getTriggerListenerSubject());
        verify(dynamicChannelLoader, times(1)).channelExists(MODULE_NAME);
        verify(dynamicChannelLoader, never()).validateTrigger(MODULE_NAME, triggerInformation.getTriggerListenerSubject());

        assertEquals(1, errors.size());
        assertEquals("task.validation.error.triggerChannelNotRegistered", errors.iterator().next().getMessage());
    }

    @Test
    public void shouldReturnChannelNotFoundErrorIfTriggerDoesNotExist() throws Exception {

        TaskTriggerInformation triggerInformation = prepareTaskTriggerInformation(prepareTrigger());

        when(channelsDataService.countFindByModuleName(MODULE_NAME)).thenReturn((long) 0);
        when(dynamicChannelLoader.channelExists(MODULE_NAME)).thenReturn(true);
        when(dynamicChannelLoader.validateTrigger(MODULE_NAME, triggerInformation.getSubject()))
                .thenReturn(false);

        Set<TaskError> errors = triggerEventService.validateTrigger(triggerInformation);

        verify(channelsDataService, times(1)).countFindByModuleName(MODULE_NAME);
        verify(triggerEventsDataService, never()).countByChannelModuleNameAndSubject(MODULE_NAME,
                triggerInformation.getSubject());
        verify(dynamicChannelLoader, times(1)).channelExists(MODULE_NAME);
        verify(dynamicChannelLoader, times(1)).validateTrigger(MODULE_NAME,
                triggerInformation.getSubject());

        assertEquals(1, errors.size());
        assertEquals("task.validation.error.triggerNotExist", errors.iterator().next().getMessage());
    }

    private List<TriggerEvent> prepareTriggers() {
        List<TriggerEvent> triggers = new ArrayList<>();

        triggers.add(new TriggerEvent(
                "Job: test_event_1-job_id1",
                "test_event_1-job_id1",
                null,
                new ArrayList<>(),
                "test_event_1"
        ));

        triggers.add(new TriggerEvent(
                "Job: test_event_2-job_id2",
                "test_event_2-job_id2",
                null,
                new ArrayList<>(),
                "test_event_2"
        ));

        triggers.add(new TriggerEvent(
                "Job: test_event_3-job_id3",
                "test_event_3-job_id3",
                null,
                new ArrayList<>(),
                "test_event_3"
        ));

        triggers.add(new TriggerEvent(
                "Job: test_event_4-job_id4",
                "test_event_4-job_id4",
                null,
                new ArrayList<>(),
                "test_event_4"
        ));

        triggers.add(new TriggerEvent(
                "Job: test_event_5-job_id5-runonce",
                "test_event_5-job_id5-runonce",
                null,
                new ArrayList<>(),
                "test_event_5"
        ));

        triggers.add(new TriggerEvent(
                "Job: test_event_6-job_id6-repeat",
                "test_event_6-job_id6-repeat",
                null,
                new ArrayList<>(),
                "test_event_6"
        ));

        return triggers;
    }

    private TriggerEvent prepareTrigger() {
        return new TriggerEvent(
                "Job: test_event_4-job_id4",
                "test_event_4-job_id4",
                null,
                new ArrayList<>(),
                "test_event_4"
        );
    }

    private TaskTriggerInformation prepareTaskTriggerInformation(TriggerEvent trigger) {
        return new TaskTriggerInformation(
                trigger.getDisplayName(),
                null,
                MODULE_NAME,
                null,
                trigger.getSubject(),
                trigger.getTriggerListenerSubject()
        );
    }
}