package org.motechproject.scheduler.tasks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduler.service.MotechSchedulerDatabaseService;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.TaskTriggerInformation;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.TriggerRetrievalException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SchedulerChannelProviderTest {

    @Mock
    private MotechSchedulerDatabaseService databaseService;

    private SchedulerChannelProvider provider;

    @Before
    public void setUp() {
        initMocks(this);
        provider = new SchedulerChannelProvider(databaseService);
    }

    @Test
    public void shouldGetTriggers() throws Exception {

        int page = 1;
        int pageSize = 10;
        List<TriggerEvent> expectedTrigger = createTriggers();

        when(databaseService.getTriggers(page, pageSize)).thenReturn(expectedTrigger);

        List<TriggerEvent> triggers = provider.getTriggers(page, pageSize);

        assertEquals(expectedTrigger, triggers);
    }

    @Test(expected = TriggerRetrievalException.class)
    public void shouldThrowTriggerRetrievalExceptionIfRetrievalFromDatabaseFailedWhenGettingTriggers() throws Exception {

        int page = 1;
        int pageSize = 10;

        when(databaseService.getTriggers(page, pageSize)).thenThrow(new SQLException());

        provider.getTriggers(page, pageSize);
    }

    @Test
    public void shouldGetTrigger() throws Exception {

        TriggerEvent expectedTrigger = createTrigger();
        TaskTriggerInformation info = new TaskTriggerInformation(
                expectedTrigger.getDisplayName(),
                expectedTrigger.getChannel().getDisplayName(),
                expectedTrigger.getChannel().getModuleName(),
                expectedTrigger.getChannel().getModuleVersion(),
                expectedTrigger.getSubject(),
                expectedTrigger.getTriggerListenerSubject()
        );

        when(databaseService.getTrigger(expectedTrigger.getSubject())).thenReturn(expectedTrigger);

        TriggerEvent trigger = provider.getTrigger(info);

        assertEquals(expectedTrigger, trigger);
    }

    @Test(expected = TriggerRetrievalException.class)
    public void shouldThrowTriggerRetrievalExceptionIfRetrievalFromDatabaseFailedWhenGettingTrigger() throws Exception {

        TaskTriggerInformation info = new TaskTriggerInformation(
                "displayName",
                "channelName",
                "moduleName",
                "moduleVersion",
                "subject",
                "triggerListener"
        );

        when(databaseService.getTrigger(info.getSubject())).thenThrow(new SQLException());

        provider.getTrigger(info);
    }

    @Test
    public void shouldCountTriggers() throws Exception {

        long expectedTriggersCount = 145;

        when(databaseService.countTriggers()).thenReturn(expectedTriggersCount);

        long actualTriggerCount = provider.countTriggers();

        assertEquals(expectedTriggersCount, actualTriggerCount);
    }

    @Test(expected = TriggerRetrievalException.class)
    public void shouldThrowTriggerRetrievalExceptionIfRetrievalFromDatabaseFailedWhenCountingTriggers() throws Exception {


        when(databaseService.countTriggers()).thenThrow(new SQLException());

        provider.countTriggers();
    }

    @Test
    public void shouldValidateSubject() throws Exception {

        String subject = "some-subject";

        assertTrue(provider.validateSubject(subject));
    }

    @Test
    public void shouldFailToValidateSubjectIfStringIsNull() throws Exception {

        String subject = null;

        assertFalse(provider.validateSubject(subject));
    }

    private List<TriggerEvent> createTriggers() {
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

    private TriggerEvent createTrigger() {

        TriggerEvent event = new TriggerEvent(
                "Job: test_event_4-job_id4",
                "test_event_4-job_id4",
                null,
                new ArrayList<>(),
                "test_event_4"
        );

        Channel channel = new Channel();
        channel.setDisplayName("test-channel");
        channel.setModuleName("some.test.channel.module");
        channel.setModuleVersion("module.version");
        event.setChannel(channel);

        return event;
    }
}