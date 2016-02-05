package org.motechproject.metrics.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.metrics.service.HealthCheckRegistryService;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class HealthCheckRegistryServiceBundleIT {
    @Inject
    private HealthCheckRegistryService healthCheckRegistryService;

    @Test
    public void shouldRegisterHealthCheckRegistryServiceInstance() {
        assertNotNull(healthCheckRegistryService);
    }
}
