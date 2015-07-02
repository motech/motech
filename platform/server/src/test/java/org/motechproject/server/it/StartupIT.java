package org.motechproject.server.it;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.testing.utils.PollingHttpClient;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class StartupIT extends BaseIT {

    @Before
    public void setUp() throws Exception {
        waitForTomcat();
        createAdminUser();
        login();
    }

    @Test
    public void shouldStartServerAndMakeAllBundlesActive() throws Exception {
        JSONArray bundles = null;

        int retryCount = 10;
        boolean starting = true;

        do {
            try {
                bundles = getBundleStatusFromServer(httpClient);
                starting = areBundlesStillStarting(bundles);

                if (!starting) {
                    logger.info("All bundles are started");
                    break;
                }

            } catch (org.apache.http.client.HttpResponseException e) {
                if (!e.getMessage().contains("Not Found")) {
                    throw e;
                }
            }

            logger.info("Wait {} milliseconds before next retry", ONE_MINUTE);
            Thread.sleep(ONE_MINUTE);
        } while (--retryCount > 0);

        assertNotNull("The bundle list cannot be empty", bundles);
        assertFalse("Failed to start bundles (TIMEOUT)", starting);
        assertBundlesStatus(bundles);
    }

    private void assertBundlesStatus(JSONArray bundles) throws JSONException {
        logger.info("Assert bundles status");

        for (int i = 0; i < bundles.length(); ++i) {
            JSONObject object = bundles.getJSONObject(i);

            String status = object.getString("state");
            String symbolicName = object.getString("symbolicName");

            logger.info("The bundle {} is in {} status", symbolicName, status);

            if (symbolicName.startsWith("org.motechproject.motech")) {
                assertEquals(symbolicName + " not active after server startup. [" + status + "]", "ACTIVE", status);
            }
        }
    }

    private boolean areBundlesStillStarting(JSONArray bundles) throws JSONException {
        logger.info("Check if bundles still starting");

        for (int i = 0; i < bundles.length(); ++i) {
            JSONObject object = bundles.getJSONObject(i);

            String status = object.getString("state");
            String symbolicName = object.getString("symbolicName");

            logger.info("The bundle {} is in {} status", symbolicName, status);

            if ("STARTING".equalsIgnoreCase(status)) {
                logger.info("There is at least one bundle that still starting");
                return true;
            }
        }

        logger.info("There is no bundle that still starting");
        return false;
    }

    private JSONArray getBundleStatusFromServer(PollingHttpClient httpClient) throws IOException, JSONException, InterruptedException {
        logger.info("Trying to get a list of bundles installed in MOTECH");

        String uri = String.format("http://%s:%d/motech-platform-server/module/admin/api/bundles", HOST, PORT);
        String response = httpClient.execute(new HttpGet(uri), new BasicResponseHandler());
        logger.info("Collected the list of bundles installed in MOTECH");

        assertNotNull(response, "Unable to retrieve bundle status from server");

        return new JSONArray(response);
    }

}
