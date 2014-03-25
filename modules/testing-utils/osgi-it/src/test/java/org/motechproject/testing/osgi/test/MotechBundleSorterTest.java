package org.motechproject.testing.osgi.test;

import org.junit.Test;
import org.motechproject.testing.osgi.MotechBundleSorter;

import static org.junit.Assert.assertEquals;

public class MotechBundleSorterTest {

    @Test
    public void shouldSortMotechPlatformBundlesInLexicalOrder() {
        String[] bundles = { "org.motechproject,motech-platform-server-config,0.17",
                "org.motechproject,motech-platform-commons-date,0.17", };

        String[] sorted = MotechBundleSorter.sort(bundles);

        assertEquals(sorted[0], "org.motechproject,motech-platform-commons-date,0.17");
        assertEquals(sorted[1], "org.motechproject,motech-platform-server-config,0.17");
    }

    @Test
    public void shouldSortPlatformBundlesBeforeModuleBundles() {
        String[] bundles = { "org.motechproject,motech-commcare-api,0.17",
                "org.motechproject,motech-platform-server-config,0.17",
                "org.motechproject,motech-platform-commons-date,0.17" };

        String[] sorted = MotechBundleSorter.sort(bundles);

        assertEquals(sorted[0], "org.motechproject,motech-platform-commons-date,0.17");
        assertEquals(sorted[1], "org.motechproject,motech-platform-server-config,0.17");
        assertEquals(sorted[2], "org.motechproject,motech-commcare-api,0.17");
    }

    @Test
    public void shouldSortThirdPartyBundleBeforeMotechBundles() {
        String[] bundles = { "org.motechproject,motech-commcare-api,0.17",
                "org.motechproject,motech-platform-server-config,0.17",
                "org.motechproject,motech-platform-commons-date,0.17",
                "javax.activation,com.springsource.javax.activation,1.1.1",
                "org.slf4j,com.springsource.slf4j.org.apache.commons.logging,1.6.1",
                "org.springframework,org.springframework.beans,3.1.0.RELEASE",
                "org.springframework,org.springframework.core,3.1.0.RELEASE" };

        String[] sorted = MotechBundleSorter.sort(bundles);

        assertEquals(sorted[0], "javax.activation,com.springsource.javax.activation,1.1.1");
        assertEquals(sorted[1], "org.slf4j,com.springsource.slf4j.org.apache.commons.logging,1.6.1");
        assertEquals(sorted[2], "org.springframework,org.springframework.beans,3.1.0.RELEASE");
        assertEquals(sorted[3], "org.springframework,org.springframework.core,3.1.0.RELEASE");
        assertEquals(sorted[4], "org.motechproject,motech-platform-commons-date,0.17");
        assertEquals(sorted[5], "org.motechproject,motech-platform-server-config,0.17");
        assertEquals(sorted[6], "org.motechproject,motech-commcare-api,0.17");
    }
}
