package org.motechproject.admin.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AdminBundleIT.class, NotificationRulesDataServiceBundleIT.class,
        StatusMessagesDataServiceBundleIT.class})
public class AdminIntegrationTests {
}
