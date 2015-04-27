package org.motechproject.testing.osgi.http;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Simple HTTP client with retries
 */

public final class SimpleHttpClient {
    public static final String NUM_TRIES_OPTION = "org.motechproject.testing.osgi.http.numTries";
    public static final String MS_WAIT_OPTION = "org.motechproject.testing.osgi.http.msWait";
    private static final int NUM_TRIES;
    private static final int NUM_TRIES_DEFAULT = 3;
    private static final int MS_WAIT;
    private static final int MS_WAIT_DEFAULT = 5000;
    private static final int MS_PER_SEC = 1000;
    private static final double SEC_WAIT;

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpClient.class);

    private SimpleHttpClient() { }

    /*
     * Set the following system properties in your IT's constructor or globally in your test project to configure
     * different default retry/wait values:
     *
     *  org.motechproject.testing.osgi.http.numTries
     *  org.motechproject.testing.osgi.http.msWait
     *
     */
    static  {
        String numTriesProp = System.getProperty(NUM_TRIES_OPTION);
        int numTries = NUM_TRIES_DEFAULT;
        String msWaitProp = System.getProperty(MS_WAIT_OPTION);
        int msWait = MS_WAIT_DEFAULT;

        if (numTriesProp != null) {
            try {
                numTries = Integer.parseInt(numTriesProp);
            } catch (NumberFormatException e) {
                LOGGER.warn("Invalid value for {}: {}", NUM_TRIES_OPTION, numTriesProp);
            }
        }

        if (msWaitProp != null) {
            try {
                msWait = Integer.parseInt(msWaitProp);
            } catch (NumberFormatException e) {
                LOGGER.warn("Invalid value for {}: {}", MS_WAIT_OPTION, msWaitProp);
            }
        }

        NUM_TRIES = numTries;
        MS_WAIT = msWait;
        SEC_WAIT = MS_WAIT / MS_PER_SEC;
    }

    /**
     * Executes the given request and returns true if the response status code is SC_OK, or false otherwise
     *
     * @param request
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public static boolean execHttpRequest(HttpUriRequest request)
            throws InterruptedException, IOException {
        return doExecHttpRequest(request, HttpStatus.SC_OK, null, null, null);
    }

    /**
     * Executes the given request and returns true if the response status code matches the given expectedStatus, or
     * false otherwise
     *
     * @param request
     * @param expectedStatus
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public static boolean execHttpRequest(HttpUriRequest request, int expectedStatus)
            throws InterruptedException, IOException {
        return doExecHttpRequest(request, expectedStatus, null, null, null);
    }

    /**
     * Executes the given request and returns true if the response body matches the given expectedResponseBody, or
     * false otherwise
     *
     * @param request
     * @param expectedResponseBody
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public static boolean execHttpRequest(HttpUriRequest request, String expectedResponseBody)
            throws InterruptedException, IOException {
        return doExecHttpRequest(request, HttpStatus.SC_OK, expectedResponseBody, null, null);
    }

    /**
     * Executes the given request and returns true if the response body matches the given expectedResponsePattern, or
     * false otherwise
     *
     * @param request
     * @param expectedResponsePattern
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public static boolean execHttpRequest(HttpUriRequest request, Pattern expectedResponsePattern)
            throws InterruptedException, IOException {
        return doExecHttpRequest(request, HttpStatus.SC_OK, expectedResponsePattern, null, null);
    }

    /**
     * Executes the given request with the given username/password auth and returns true if the response status is
     * HTTP 200,  or false otherwise
     *
     * @param request
     * @param username - may be null
     * @param password - may be null
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public static boolean execHttpRequest(HttpUriRequest request, String username, String password)
            throws InterruptedException, IOException {
        return doExecHttpRequest(request, HttpStatus.SC_OK, null, username, password);
    }

    /**
     * Executes the given request with the given username/password auth and returns true if the response body matches
     * the given expectedResponseBody, or false otherwise
     *
     * @param request
     * @param expectedResponseBody
     * @param username - may be null
     * @param password - may be null
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public static boolean execHttpRequest(HttpUriRequest request, String expectedResponseBody, String username,
                                          String password)
            throws InterruptedException, IOException {
        return doExecHttpRequest(request, HttpStatus.SC_OK, expectedResponseBody, username, password);
    }

    /**
     * Executes the given request with the given username/password auth and returns true if the response body matches
     * the given expectedResponsePattern, or false otherwise
     *
     * @param request
     * @param expectedResponsePattern
     * @param username - may be null
     * @param password - may be null
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public static boolean execHttpRequest(HttpUriRequest request, Pattern expectedResponsePattern, String username,
                                          String password)
            throws InterruptedException, IOException {
        return doExecHttpRequest(request, HttpStatus.SC_OK, expectedResponsePattern, username, password);
    }

    /**
     * Executes the given request with the given username/password auth and returns true if the response body matches
     * the given expectedResponseBody and the response status matches the given expectedStatus, or false otherwise
     *
     * @param request
     * @param expectedStatus
     * @param expectedResponseBody
     * @param username - may be null
     * @param password - may be null
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public static boolean execHttpRequest(HttpUriRequest request, int expectedStatus, String expectedResponseBody,
                                          String username, String password)
            throws InterruptedException, IOException {
        return doExecHttpRequest(request, expectedStatus, expectedResponseBody, username, password);
    }

    /**
     * Executes the given request with the given username/password auth and returns true if the response body matches
     * the given expectedResponsePattern and the response status matches the given expectedStatus, or false otherwise
     *
     * @param request
     * @param expectedStatus
     * @param expectedResponsePattern
     * @param username - may be null
     * @param password - may be null
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public static boolean execHttpRequest(HttpUriRequest request, int expectedStatus, Pattern expectedResponsePattern,
                                          String username, String password)
            throws InterruptedException, IOException {
        return doExecHttpRequest(request, expectedStatus, expectedResponsePattern, username, password);
    }

    /**
     * Executes the given request with the given username/password auth and returns true if the response status matches
     * the given expectedStatus,  or false otherwise
     *
     * @param request
     * @param expectedStatus
     * @param username - may be null
     * @param password - may be null
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public static boolean execHttpRequest(HttpUriRequest request, int expectedStatus, String username, String password)
            throws InterruptedException, IOException {
        return doExecHttpRequest(request, expectedStatus, null, username, password);
    }

    private static DefaultHttpClient createHttpClient(String u, String p) {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        if (!StringUtils.isBlank(u)) {
            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(u, p);
            provider.setCredentials(AuthScope.ANY, credentials);
            httpClient.setCredentialsProvider(provider);
        }

        return httpClient;
    }

    private static boolean doExecHttpRequest(HttpUriRequest request, int expectedStatus,
                                             Object expectedResponseBodyObject, String username, String password)
            throws InterruptedException, IOException {
        int tries = 0;
        do {
            tries++;

            DefaultHttpClient httpClient = createHttpClient(username, password);

            HttpResponse response = httpClient.execute(request);
            if (expectedStatus == response.getStatusLine().getStatusCode()) {
                LOGGER.debug(String.format("Successfully received HTTP %d in %d %s", expectedStatus, tries,
                        tries == 1 ? "try" : "tries"));
                if (expectedResponseBodyObject == null) {
                    return true;
                }

                String responseBody = EntityUtils.toString(response.getEntity());
                if (expectedResponseBodyObject instanceof Pattern) {
                    Pattern expectedResponseBodyPattern = (Pattern) expectedResponseBodyObject;
                    if (expectedResponseBodyPattern.matcher(responseBody).matches()) {
                        return true;
                    }
                    LOGGER.debug("Response body:\n{}\nfails to match:\n{}.", responseBody,
                            expectedResponseBodyPattern.pattern());
                    return false;
                } else {
                    // assume it's a String
                    String expectedResponseBodyString = expectedResponseBodyObject.toString();
                    if (StringUtils.isBlank(expectedResponseBodyString)) {
                        return true;
                    }
                    if (responseBody.equals(expectedResponseBodyString)) {
                        return true;
                    }
                    LOGGER.debug("Expected {} but received {}.", expectedResponseBodyString, responseBody);
                    return false;
                }
            }
            LOGGER.debug(String.format("Was expecting HTTP %d but received %d, trying again in %f", expectedStatus,
                    response.getStatusLine().getStatusCode(), SEC_WAIT));
            if (tries < NUM_TRIES) {
                Thread.sleep(MS_WAIT);
            }
        } while (tries < NUM_TRIES);

        LOGGER.debug("Giving up trying to receive HTTP {} after {} tries", expectedStatus, NUM_TRIES);
        return false;
    }
}
