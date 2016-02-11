package org.motechproject.testing.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A facade over the {@link org.apache.http.client.HttpClient}, allowing polling of urls.
 * The client will continue retries if there is no response, or if the response has a not expected error code.
 * If an error code is expected, it should be provided beforehand, so that the retries stop once they hit it.
 */
public class PollingHttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(PollingHttpClient.class);

    public static final int MILLIS_PER_SEC = 1000;

    private DefaultHttpClient httpClient;
    private int maxWaitPeriodInSeconds;

    public static final int ERROR_NOT_EXPECTED = -1;

    /**
     * Creates an instance with a new instance of {@link DefaultHttpClient} underneath and
     * timeout of 1 minute.
     */
    public PollingHttpClient() {
        this(new DefaultHttpClient(), 60);
    }

    /**
     * Creates an instance with a new instance of {@link DefaultHttpClient} underneath and timeout of
     * {@code waitPeriodInSeconds} seconds.
     *
     * @param waitPeriodInSeconds the time that the client will wait for satisfying response, in seconds
     */
    public PollingHttpClient(int waitPeriodInSeconds) {
        this(new DefaultHttpClient(), waitPeriodInSeconds);
    }

    /**
     * Creates an instance with the given instance of {@link DefaultHttpClient} underneath and
     * the given timeout time.
     * @param httpClient the http client that is being decorated by this class
     * @param waitPeriodInSeconds the time that the client will wait for a satysfying response, in seconds
     */
    public PollingHttpClient(DefaultHttpClient httpClient, int waitPeriodInSeconds) {
        this.httpClient = httpClient;
        this.maxWaitPeriodInSeconds = waitPeriodInSeconds;
    }

    /**
     * Returns the maximum time this client will wait when polling an HTTP address, across all retries.
     * @return the maximum wait time, in seconds
     */
    public int getMaxWaitPeriodInSeconds() {
        return maxWaitPeriodInSeconds;
    }

    /**
     * Sets the maximum time this client will wait when polling an HTTP address, across all retries.
     * @param maxWaitPeriodInSeconds the maximum wait time, in seconds
     */
    public void setMaxWaitPeriodInSeconds(int maxWaitPeriodInSeconds) {
        this.maxWaitPeriodInSeconds = maxWaitPeriodInSeconds;
    }

    /**
     * Executes a get of the given URI. Will poll the URI until it receives a success status or times out.
     * @param uri the URI to get
     * @return the HTTP Response from the URI
     * @throws IOException if there was a communication error
     * @throws InterruptedException if the client was interrupted while polling
     */
    public HttpResponse get(String uri) throws IOException, InterruptedException {
        return get(uri, new DefaultResponseHandler());
    }

    /**
     * Executes the given HTTP request. Will poll the URI until it receives a success status or times out.
     * @param request the request to execute.
     * @return the HTTP Response from the URI
     * @throws IOException if there was a communication error
     * @throws InterruptedException if the client was interrupted while polling
     */
    public HttpResponse execute(HttpUriRequest request) throws IOException, InterruptedException {
        return execute(request, new DefaultResponseHandler(), ERROR_NOT_EXPECTED);
    }

    /**
     * Executes the given HTTP request. Will poll the URI until it receives the expected error code or times out.
     * @param request the request to execute.
     * @param expectedErrorCode the error code expected, when it is received it will end the polling
     * @return the HTTP Response from the URI
     * @throws IOException if there was a communication error
     * @throws InterruptedException if the client was interrupted while polling
     */
    public HttpResponse execute(HttpUriRequest request, int expectedErrorCode) throws IOException, InterruptedException {
        return execute(request, new DefaultResponseHandler(), expectedErrorCode);
    }

    /**
     * Executes a get of the given URI. Will poll the URI until it receives a success status or times out.
     * @param uri the URI to get
     * @param responseHandler a handler that will parse the response
     * @param <T> the type to which the handler parses the response
     * @return the parsed response
     * @throws IOException if there was a communication error
     * @throws InterruptedException if the client was interrupted while polling
     */
    public <T> T get(String uri, final ResponseHandler<? extends T> responseHandler) throws IOException, InterruptedException {
        return execute(new HttpGet(uri), responseHandler, ERROR_NOT_EXPECTED);
    }

    /**
     * Executes a get of the provided http request. Will poll until it receives a success status or times out.
     * @param request the request to execute
     * @param responseHandler a handler that will parse the response
     * @param <T> the type to which the handler parses the response
     * @return the parsed response
     * @throws IOException if there was a communication error
     * @throws InterruptedException if the client was interrupted while polling
     */
    public <T> T execute(HttpUriRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException, InterruptedException {
        return execute(request, responseHandler, ERROR_NOT_EXPECTED);
    }

    /**
     * Executes a get of the provided http request. Will poll until it receives a success code, the expected error code or times out.
     * @param request the request to execute
     * @param responseHandler a handler that will parse the response
     * @param expectedErrorCode the expected error code, will stop polling if it is received
     * @param <T> the type to which the handler parses the response
     * @return the parsed response
     * @throws IOException if there was a communication error
     * @throws InterruptedException if the client was interrupted while polling
     */
    public <T> T execute(HttpUriRequest request, final ResponseHandler<? extends T> responseHandler,
                         int expectedErrorCode) throws IOException, InterruptedException {
        return executeWithWaitForUriAvailability(request, responseHandler, expectedErrorCode);
    }

    /**
     * Returns the {@link CredentialsProvider} for the underlying client instance. Can be used to set authentication
     * information.
     * @return the credentials provider
     */
    public CredentialsProvider getCredentialsProvider() {
        return httpClient.getCredentialsProvider();
    }

    /**
     * Sets the cookie store that will be used by the underlying client.
     * @param cookieStore the cookie store to use
     */
    public void setCookieStore(CookieStore cookieStore) {
        httpClient.setCookieStore(cookieStore);
    }

    private <T> T executeWithWaitForUriAvailability(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler,
                                                    int expectedErrorCode) throws InterruptedException, IOException {
        final long timeoutInMillis = maxWaitPeriodInSeconds * 1000;
        
        long waitingFor;
        HttpResponse response = null;

        long startTime = System.currentTimeMillis();
        try {
            do {
                try {
                    if (response != null) {
                        EntityUtils.consume(response.getEntity());
                    }

                    response = httpClient.execute(httpUriRequest);

                    if (responseNotFound(response, expectedErrorCode)) {
                        if (response == null) {
                            LOGGER.warn("Response not found. Thread stopped for 2 seconds.");
                        } else {
                            LOGGER.warn("Wrong response status: {}. Thread stopped for 2 seconds",
                                    response.getStatusLine().getStatusCode());
                        }
                        Thread.sleep(2 * MILLIS_PER_SEC);
                    }
                } catch (IOException e) {
                    LOGGER.error("Error while executing request. Stopping thread for 2 seconds", e);
                    Thread.sleep(2 * MILLIS_PER_SEC);
                }

                waitingFor = System.currentTimeMillis() - startTime;
            } while (responseNotFound(response, expectedErrorCode) && waitingFor < timeoutInMillis);

            return response == null ? null : responseHandler.handleResponse(response);
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }

    private static boolean responseNotFound(HttpResponse response, int expectedErrorCode) {
        if (response == null) {
            return true;
        }

        int statusCode = response.getStatusLine().getStatusCode();

        if (expectedErrorCode == ERROR_NOT_EXPECTED) {
            return statusCode >= HttpStatus.SC_BAD_REQUEST;
        } else {
            return statusCode != expectedErrorCode;
        }
    }

    private class DefaultResponseHandler implements ResponseHandler<HttpResponse> {

        @Override
        public HttpResponse handleResponse(HttpResponse response) throws IOException {
            return response;
        }

    }
}







