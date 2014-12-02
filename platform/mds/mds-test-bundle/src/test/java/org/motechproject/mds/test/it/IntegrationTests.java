package org.motechproject.mds.test.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.motechproject.mds.test.osgi.MdsDdeBundleIT;
import org.motechproject.mds.test.osgi.MdsDdeValidationIT;

@RunWith(Suite.class)
@Suite.SuiteClasses({MdsDdeBundleIT.class, MdsDdeValidationIT.class})
public class IntegrationTests {
}
