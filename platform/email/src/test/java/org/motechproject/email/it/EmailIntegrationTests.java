package org.motechproject.email.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.motechproject.email.osgi.EmailBundleIT;
import org.motechproject.email.osgi.EmailChannelBundleIT;
import org.motechproject.email.repository.EmailRecordServiceIT;
import org.motechproject.email.service.impl.EmailAuditServiceIT;

@RunWith(Suite.class)
@Suite.SuiteClasses({EmailBundleIT.class,
                     EmailAuditServiceIT.class,
                     EmailRecordServiceIT.class,
                     EmailChannelBundleIT.class})
public class EmailIntegrationTests {
}
