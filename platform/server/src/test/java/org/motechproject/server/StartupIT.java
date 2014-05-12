package org.motechproject.server;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class StartupIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartupIT.class);
    private static final Long ONE_MINUTE = 60 * 1000L;
    private static final String HOST = "localhost";
    private static final int PORT = TestContext.getTomcatPort();
    private static final PollingHttpClient httpClient;

    static {
        LOGGER.info("The tomcat server is available at {}:{}", HOST, PORT);

        httpClient = new PollingHttpClient();
        httpClient.setCookieStore(new BasicCookieStore());
        LOGGER.info("The http client has been initialized.");
    }

    @Test
    public void shouldStartServerAndMakeAllBundlesActive() throws Exception {
        waitForTomcat();

        login();
        JSONArray bundles = null;

        int retryCount = 10;
        boolean starting = true;

        do {
            try {
                bundles = getBundleStatusFromServer(httpClient);
                starting = isBundlesStillStarting(bundles);

                if (!starting) {
                    LOGGER.info("All bundles are started");
                    break;
                }

            } catch (org.apache.http.client.HttpResponseException e) {
                if (!e.getMessage().contains("Not Found")) {
                    throw e;
                }
            }

            LOGGER.info("Wait {} milliseconds before next retry", ONE_MINUTE);
            Thread.sleep(ONE_MINUTE);
        } while (--retryCount > 0);

        assertNotNull("The bundle list cannot be empty", bundles);
        assertFalse("Failed to start bundles (TIMEOUT)", starting);
        assertBundlesStatus(bundles);
    }

    private void assertBundlesStatus(JSONArray bundles) throws JSONException {
        LOGGER.info("Assert bundles status");

        for (int i = 0; i < bundles.length(); ++i) {
            JSONObject object = bundles.getJSONObject(i);

            String status = object.getString("state");
            String symbolicName = object.getString("symbolicName");

            LOGGER.info("The bundle {} is in {} status", symbolicName, status);

            if (symbolicName.startsWith("org.motechproject.motech")) {
                assertEquals(symbolicName + " not active after server startup. [" + status + "]", "ACTIVE", status);
            }
        }
    }

    private boolean isBundlesStillStarting(JSONArray bundles) throws JSONException {
        LOGGER.info("Check if bundles still starting");

        for (int i = 0; i < bundles.length(); ++i) {
            JSONObject object = bundles.getJSONObject(i);

            String status = object.getString("state");
            String symbolicName = object.getString("symbolicName");

            LOGGER.info("The bundle {} is in {} status", symbolicName, status);

            if ("STARTING".equalsIgnoreCase(status)) {
                LOGGER.info("There is at least one bundle that still starting");
                return true;
            }
        }

        LOGGER.info("There is no bundle that still starting");
        return false;
    }

    private JSONArray getBundleStatusFromServer(PollingHttpClient httpClient) throws IOException, JSONException, InterruptedException {
        LOGGER.info("Trying to get a list of bundles installed in MOTECH");
        /*
            BugCard #208 remove this once we fix web authentication issue, currently till security
            modules started in osgi env there is not authentication for admin console.
        */
        login();

        String uri = String.format("http://%s:%d/motech-platform-server/module/admin/api/bundles", HOST, PORT);
        String response = httpClient.execute(new HttpGet(uri), new BasicResponseHandler());
        LOGGER.info("Collected the list of bundles installed in MOTECH");

        return new JSONArray(response);
    }

    private void login() throws IOException, InterruptedException {
        String uri = String.format("http://%s:%d/motech-platform-server/module/server/motech-platform-server/j_spring_security_check", HOST, PORT);
        String username = "motech";

        final HttpPost loginPost = new HttpPost(uri);

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("j_username", username));
        nvps.add(new BasicNameValuePair("j_password", username));

        loginPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF8"));

        LOGGER.info("Trying to login into MOTECH as {}", username);
        final HttpResponse response = httpClient.execute(loginPost);
        EntityUtils.consume(response.getEntity());
        LOGGER.info("Logged into MOTECH as {}", username);
    }

    private void waitForTomcat() throws IOException, InterruptedException {
        LOGGER.info("Waiting for tomcat");

        String uri = String.format("http://%s:%d/motech-platform-server/module/server", HOST, PORT);
        HttpGet waitGet = new HttpGet(uri);
        HttpResponse response = httpClient.execute(waitGet);
        LOGGER.info("Proceeding after getting a reponse: {}", response);

        LOGGER.info("Tomcat is running");
    }
}
