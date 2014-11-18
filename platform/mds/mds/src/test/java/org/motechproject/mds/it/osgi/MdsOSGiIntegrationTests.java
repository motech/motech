package org.motechproject.mds.it.osgi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.motechproject.mds.osgi.HistoryTrashServiceBundleIT;

@RunWith(Suite.class)
@Suite.SuiteClasses({MdsBundleIT.class, HistoryTrashServiceBundleIT.class})
public class MdsOSGiIntegrationTests {
}
