package org.motechproject.server.war.test.it;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.testing.tomcat.BaseTomcatIT;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StartupIT extends BaseTomcatIT {

    private static final String LOCATION = "location";
    private static final String JNDI_PREFIX = "jndi:";

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
            assertBundleComesFromJndi(bundles.getJSONObject(i));
            assertBundleStatus(bundles.getJSONObject(i));
        }
    }

    private void assertBundleComesFromJndi(JSONObject object) {

        String location = object.getString(LOCATION);

        assertNotNull(location);
        assertTrue(location.startsWith(JNDI_PREFIX));
    }
}
