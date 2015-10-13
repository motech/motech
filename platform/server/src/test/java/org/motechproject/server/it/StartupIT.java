package org.motechproject.server.it;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.testing.tomcat.BaseTomcatIT;

public class StartupIT extends BaseTomcatIT {

    @Before
    public void setUp() throws Exception {
        prepareTomcat();
    }

    @org.junit.Ignore
    @Test
    public void shouldStartServerAndMakeAllBundlesActive() throws Exception {

        JSONArray bundles = getBundleStatusFromServer(HTTP_CLIENT);

        waitForBundles(bundles);

        for (int i = 0; i < bundles.length(); i++) {
            assertBundleStatus(bundles.getJSONObject(i));
        }
    }
}
