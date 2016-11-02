package org.motechproject.tasks.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ActionParametersBundleIT.class, ChannelsDataServiceBundleIT.class,  TaskDataServiceBundleIT.class, TaskActivitiesDataServiceBundleIT.class,
        TasksBundleIT.class})
public class TasksIntegrationTests {
}
