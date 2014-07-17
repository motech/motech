package org.motechproject.scheduler.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({SchedulerBundleIT.class, MotechSchedulerServiceImplIT.class, SpringQuartzIT.class})
public class SchedulerIntegrationTests {
}
