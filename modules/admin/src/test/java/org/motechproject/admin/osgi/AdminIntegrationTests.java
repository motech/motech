package org.motechproject.admin.osgi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AdminBundleIT.class, NotificationRulesDataServiceIT.class, StatusMessagesDataServiceIT.class})
public class AdminIntegrationTests {
}
