package org.motechproject.osgi.web.osgi;

import org.motechproject.osgi.web.HttpServiceTracker;
import org.motechproject.osgi.web.HttpServiceTrackers;
import org.motechproject.osgi.web.UIServiceTracker;
import org.motechproject.osgi.web.UIServiceTrackers;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import java.util.jar.Manifest;

public class BlueprintContextTrackerBundleIT extends BaseOsgiIT {

    public void testThatHttpServiceTrackerWasAdded() {

        Bundle testBundle = bundleContext.getBundle();

        ServiceReference httpServiceTrackersRef = bundleContext.getServiceReference(HttpServiceTrackers.class.getName());
        assertNotNull(httpServiceTrackersRef);

        HttpServiceTrackers httpServiceTrackers = (HttpServiceTrackers) bundleContext.getService(httpServiceTrackersRef);

        HttpServiceTracker removedHttpServiceTracker = httpServiceTrackers.removeTrackerFor(testBundle);

        assertNotNull(removedHttpServiceTracker);
    }

    public void testThatUIServiceTrackerWasAdded() {

        Bundle testBundle = bundleContext.getBundle();

        ServiceReference uiServiceTrackersRef = bundleContext.getServiceReference(UIServiceTrackers.class.getName());
        assertNotNull(uiServiceTrackersRef);

        UIServiceTrackers uiServiceTrackers = (UIServiceTrackers) bundleContext.getService(uiServiceTrackersRef);

        UIServiceTracker removedUiServiceTracker = uiServiceTrackers.removeTrackerFor(testBundle);

        assertNotNull(removedUiServiceTracker);

    }


    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testWebUtilApplicationContext.xml"};
    }

    @Override
    protected Manifest getManifest() {
        Manifest manifest = super.getManifest();
        manifest.getMainAttributes().putValue("Blueprint-Enabled", "true");
        manifest.getMainAttributes().putValue("Context-File", "META-INF/spring/testWebUtilApplicationContext.xml");
        manifest.getMainAttributes().putValue("Context-Path", "/test");
        return manifest;
    }
}
