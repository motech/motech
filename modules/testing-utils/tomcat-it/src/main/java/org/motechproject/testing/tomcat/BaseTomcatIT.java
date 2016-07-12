package org.motechproject.testing.tomcat;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class BaseTomcatIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected static final Long ONE_MINUTE = 60 * 1000L;
    protected static final String HOST = "localhost";
    protected static final String MOTECH = "motech";
    protected static final int PORT = TestContext.getTomcatPort();
    protected static final PollingHttpClient HTTP_CLIENT;

    static {
        HTTP_CLIENT = new PollingHttpClient(120);
        HTTP_CLIENT.setCookieStore(new BasicCookieStore());
    }

    public void prepareTomcat() throws IOException, InterruptedException {
        waitForTomcat();
        createAdminUser();
        login();
    }

    public void waitForBundles(JSONArray bundles) throws IOException, InterruptedException {

        assertNotNull("The bundle list cannot be empty", bundles);

        int retryCount = 10;
        boolean starting;

        do {
            starting = areBundlesStillStarting(bundles);

            if (!starting) {
                logger.info("All bundles are started");
                break;
            }

            logger.info("Wait {} milliseconds before next retry", ONE_MINUTE);
            Thread.sleep(ONE_MINUTE);
        } while (--retryCount > 0);

        assertFalse("Failed to start bundles (TIMEOUT)", starting);
    }

    protected void login() throws IOException, InterruptedException {
        String uri = String.format("http://%s:%d/motech-platform-server/server/motech-platform-server/j_spring_security_check", HOST, PORT);

        final HttpPost loginPost = new HttpPost(uri);

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("j_username", MOTECH));
        nvps.add(new BasicNameValuePair("j_password", MOTECH));

        loginPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF8"));

        logger.info("Trying to login into MOTECH as {}", MOTECH);
        HttpResponse response = HTTP_CLIENT.execute(loginPost);
        logger.info("Response status: {}", response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());
        logger.info("Logged into MOTECH as {}", MOTECH);
    }

    protected void logout() throws IOException, InterruptedException {
        String uri = String.format("http://%s:%d/motech-platform-server/server/j_spring_security_logout", HOST, PORT);

        final HttpGet logoutGet = new HttpGet(uri);

        logger.info("Trying to logout from MOTECH");
        HttpResponse response = HTTP_CLIENT.execute(logoutGet);
        logger.info("Response status: {}", response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());
        logger.info("Logged out from MOTECH");
    }


    protected void createAdminUser() throws IOException, InterruptedException {
        String url = String.format("http://%s:%d/motech-platform-server/server/startup", HOST, PORT);
        String json = "{\"language\":\"en\", \"adminLogin\":\"motech\", \"adminPassword\":\"motech\", \"adminConfirmPassword\": \"motech\", \"adminEmail\":\"motech@motech.com\", \"loginMode\":\"repository\"}";

        StringEntity entity = new StringEntity(json, HTTP.UTF_8);
        entity.setContentType("application/json");

        HttpPost post = new HttpPost(url);
        post.setEntity(entity);

        logger.info("Trying to create admin user ({}) in MOTECH", MOTECH);
        HttpResponse response = HTTP_CLIENT.execute(post);
        logger.info("Response status: {}", response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());
        logger.info("Created admin user ({}) in MOTECH", MOTECH);
    }

    protected void waitForTomcat() throws IOException, InterruptedException {
        logger.info("Waiting for tomcat");

        String uri = String.format("http://%s:%d/motech-platform-server/server", HOST, PORT);
        HttpGet waitGet = new HttpGet(uri);
        HttpResponse response = HTTP_CLIENT.execute(waitGet);
        logger.info("Proceeding after getting a response: {}", response);

        logger.info("Tomcat is running");
    }

    protected void assertBundleStatus(JSONObject object) throws JSONException {

        String status = object.getString("state");
        String symbolicName = object.getString("symbolicName");

        logger.info("The bundle {} is in {} status", symbolicName, status);

        if (symbolicName.startsWith("org.motechproject.motech")) {
            assertEquals(symbolicName + " not active after server startup. [" + status + "]", "ACTIVE", status);
        }
    }

    protected boolean areBundlesStillStarting(JSONArray bundles) throws JSONException {
        logger.info("Check if bundles are still starting");

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

    protected JSONArray getBundleStatusFromServer(PollingHttpClient httpClient) throws IOException, JSONException, InterruptedException {
        logger.info("Trying to get a list of bundles installed in MOTECH");

        String uri = String.format("http://%s:%d/motech-platform-server/admin/api/bundles", HOST, PORT);

        return httpClient.execute(new HttpGet(uri), new ResponseHandler<JSONArray>() {
            @Override
            public JSONArray handleResponse(HttpResponse response) throws IOException {
                logger.info("Collected the list of bundles installed in MOTECH");

                assertNotNull("Unable to retrieve bundle status from server", response);

                logger.debug("Server response status for bundle status request: {}", response.getStatusLine());

                String responseBody = EntityUtils.toString(response.getEntity());

                logger.debug("Response body: {}", responseBody);

                return new JSONArray(responseBody);
            }
        });
    }
}
