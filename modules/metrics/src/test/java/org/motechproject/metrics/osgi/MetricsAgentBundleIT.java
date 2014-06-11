package org.motechproject.metrics.osgi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.metrics.service.MetricsAgent;
import org.motechproject.testing.osgi.BasePaxIT;
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
public class MetricsAgentBundleIT extends BasePaxIT {

    @Inject
    private MetricsAgent metricsAgent;

    @Test
    public void testMetricsAgentLoads() {
        assertNotNull(metricsAgent);
    }
}

