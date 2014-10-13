package org.motechproject.email.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({EmailBundleIT.class,
                     EmailAuditServiceBundleIT.class,
                     EmailRecordServiceBundleIT.class,
                     EmailChannelBundleIT.class})
public class EmailIntegrationTests {
}
