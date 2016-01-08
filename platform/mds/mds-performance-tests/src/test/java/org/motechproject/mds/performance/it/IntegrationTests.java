package org.motechproject.mds.performance.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.motechproject.mds.performance.osgi.MdsDiskSpaceUsageIT;
import org.motechproject.mds.performance.osgi.MdsDummyDataGeneratorIT;
import org.motechproject.mds.performance.osgi.MdsStressIT;

/**
 * These tests are not a part of IT profile, so they don't run with mvn clean install -PIT.
 * Instead, they are run with profile "MDSP"
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({MdsDummyDataGeneratorIT.class, MdsStressIT.class, MdsDiskSpaceUsageIT.class})
public class IntegrationTests {
}
