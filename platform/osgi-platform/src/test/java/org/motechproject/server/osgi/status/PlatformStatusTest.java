package org.motechproject.server.osgi.status;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlatformStatusTest {

    @Test
    public void shouldCalculatePercentageCorrectly() {
        PlatformStatus platformStatus = new PlatformStatus();

        assertEquals(0, platformStatus.getStartupProgressPercentage());

        platformStatus.addStartedBundle("org.motechproject.motech-platform-osgi-web-utils");
        platformStatus.addStartedBundle("org.motechproject.motech-platform-commons-sql");

        assertEquals(20, platformStatus.getStartupProgressPercentage());

        platformStatus.addStartedBundle("org.motechproject.motech-platform-config-core");
        platformStatus.addStartedBundle("org.motechproject.motech-platform-event");
        platformStatus.addStartedBundle("org.motechproject.motech-platform-dataservices");
        platformStatus.addStartedBundle("org.motechproject.motech-platform-dataservices-entities");

        assertEquals(60, platformStatus.getStartupProgressPercentage());

        platformStatus.addStartedBundle("org.motechproject.motech-platform-email");
        platformStatus.addStartedBundle("org.motechproject.motech-platform-server-config");
        platformStatus.addStartedBundle("org.motechproject.motech-platform-web-security");
        platformStatus.addStartedBundle("org.motechproject.motech-platform-server-bundle");

        assertEquals(100, platformStatus.getStartupProgressPercentage());

        platformStatus.addStartedBundle("Some other module");

        // should not got over 100
        assertEquals(100, platformStatus.getStartupProgressPercentage());

        // make sure that the setter also updates the percentage
        PlatformStatus other = new PlatformStatus();
        other.setStartedBundles(platformStatus.getStartedBundles());
        assertEquals(100, other.getStartupProgressPercentage());
    }
}
