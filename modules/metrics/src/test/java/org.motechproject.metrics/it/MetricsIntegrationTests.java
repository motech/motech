package org.motechproject.metrics.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({MetricRegistryServiceBundleIT.class, HealthCheckRegistryServiceBundleIT.class})
public class MetricsIntegrationTests {
}
