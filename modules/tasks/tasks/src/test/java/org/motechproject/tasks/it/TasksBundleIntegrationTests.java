package org.motechproject.tasks.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This is a separate Suite, because TasksBundleIT relies on bundle registration/deregistration
 * mechanics.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({TasksBundleIT.class})
public class TasksBundleIntegrationTests {
}
