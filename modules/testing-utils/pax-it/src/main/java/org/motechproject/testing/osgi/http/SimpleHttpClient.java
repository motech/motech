package org.motechproject.testing.osgi.http;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Simple HTTP client with retries
 */
public final class SimpleHttpClient {
    private static final int NUM_TRIES = 3;
    private static final int MS_WAIT = 5000;
    private static final int MS_PER_SEC = 1000;
    private static final double SEC_WAIT = MS_WAIT / MS_PER_SEC;

    private static Logger logger = LoggerFactory.getLogger(SimpleHttpClient.class);

    private SimpleHttpClient() { }

    public static boolean execHttpRequest(HttpUriRequest request, int expectedStatus)
            throws InterruptedException, IOException {
        return execHttpRequest(request, expectedStatus, null, null);
    }

    public static boolean execHttpRequest(HttpUriRequest request, int expectedStatus, String username, String password)
            throws InterruptedException, IOException {
        int tries = 0;
        do {
            tries++;

            DefaultHttpClient httpClient = new DefaultHttpClient();

            if (!StringUtils.isBlank(username)) {
                CredentialsProvider provider = new BasicCredentialsProvider();
                UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
                provider.setCredentials(AuthScope.ANY, credentials);
                httpClient.setCredentialsProvider(provider);
            }

            HttpResponse response = httpClient.execute(request);
            if (expectedStatus == response.getStatusLine().getStatusCode()) {
                logger.debug(String.format("Successfully received HTTP %d in %d %s", expectedStatus, tries,
                        tries == 1 ? "try" : "tries"));
                return true;
            }
            logger.debug(String.format("Was expecting HTTP %d but received %d, trying again in %f", expectedStatus,
                    response.getStatusLine().getStatusCode(), SEC_WAIT));
            Thread.sleep(MS_WAIT);
        } while (tries < NUM_TRIES);

        logger.debug("Giving up trying to receive HTTP {} after {} tries", expectedStatus, NUM_TRIES);
        return false;
    }
}
