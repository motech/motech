package org.motechproject.osgi.web.osgi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.osgi.web.HttpServiceTracker;
import org.motechproject.osgi.web.HttpServiceTrackers;
import org.motechproject.osgi.web.UIServiceTracker;
import org.motechproject.osgi.web.UIServiceTrackers;
import org.motechproject.testing.osgi.BasePaxIT;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class BlueprintContextTrackerBundleIT extends BasePaxIT {

    @Inject
    private BundleContext bundleContext;
    @Inject
    private HttpServiceTrackers httpServiceTrackers;
    @Inject
    private UIServiceTrackers uiServiceTrackers;

    @ProbeBuilder
    public TestProbeBuilder build(TestProbeBuilder builder) {
        return builder.setHeader("Blueprint-Enabled", "true")
                .setHeader("Context-File", "META-INF/spring/testWebUtilApplicationContext.xml")
                .setHeader("Context-Path", "/test");
    }

    @Test
    public void testThatHttpServiceTrackerWasAdded() {
        Bundle testBundle = bundleContext.getBundle();

        HttpServiceTracker removedHttpServiceTracker = httpServiceTrackers.removeTrackerFor(testBundle);

        assertNotNull(removedHttpServiceTracker);
    }

    @Test
    public void testThatUIServiceTrackerWasAdded() {
        Bundle testBundle = bundleContext.getBundle();

        UIServiceTracker removedUiServiceTracker = uiServiceTrackers.removeTrackerFor(testBundle);

        assertNotNull(removedUiServiceTracker);
    }
}
