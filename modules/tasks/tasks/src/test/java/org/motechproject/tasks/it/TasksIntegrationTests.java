package org.motechproject.tasks.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ChannelsDataServiceBundleIT.class,  TaskDataServiceBundleIT.class,
        TaskActivitiesDataServiceBundleIT.class, TasksBundleIT.class, ActionParametersIT.class})
public class TasksIntegrationTests {
}
