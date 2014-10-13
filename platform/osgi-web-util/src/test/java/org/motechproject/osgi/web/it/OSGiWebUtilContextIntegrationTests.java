package org.motechproject.osgi.web.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({Log4JBundleLoaderContextIT.class, ServerLogServiceContextIT.class})
public class OSGiWebUtilContextIntegrationTests {
}
