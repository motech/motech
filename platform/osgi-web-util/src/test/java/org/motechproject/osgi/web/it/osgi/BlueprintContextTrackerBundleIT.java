package org.motechproject.osgi.web.it.osgi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.osgi.web.tracker.HttpServiceTrackers;
import org.motechproject.osgi.web.tracker.UIServiceTrackers;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.wait.Wait;
import org.motechproject.testing.osgi.wait.WaitCondition;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;

import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class BlueprintContextTrackerBundleIT extends BasePaxIT {

    private static final int WAIT_TIME = 5000;

    @Inject
    private BundleContext bundleContext;
    @Inject
    private HttpServiceTrackers httpServiceTrackers;
    @Inject
    private UIServiceTrackers uiServiceTrackers;

    @ProbeBuilder
    public TestProbeBuilder build(TestProbeBuilder builder) {
        return builder.setHeader("Blueprint-Enabled", "true")
                .setHeader("Context-Path", "/test");
    }

    @Test
    public void testThatHttpServiceTrackerWasAdded() throws InterruptedException {
        final Bundle testBundle = bundleContext.getBundle();

        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return !httpServiceTrackers.isBeingTracked(testBundle);
            }
        }, WAIT_TIME).start();

        assertTrue(httpServiceTrackers.isBeingTracked(testBundle));
    }

    @Test
    public void testThatUIServiceTrackerWasAdded() throws InterruptedException {
        final Bundle testBundle = bundleContext.getBundle();

        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return !uiServiceTrackers.isBeingTracked(testBundle);
            }
        }, WAIT_TIME).start();

        assertTrue(uiServiceTrackers.isBeingTracked(testBundle));
    }
}
