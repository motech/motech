package org.motechproject.testing.osgi.http;

import org.apache.http.HttpResponse;
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
 * A facade over the {@link org.apache.commons.httpclient.HttpClient}, allowing polling of urls.
 * The client will continue retries if there is no response, or it is an error code. If an error code
 * is expected, it should be provided beforehand, so that the retries stop once they hit it.
 */
public class PollingHttpClient {
    private static final Logger LOG = LoggerFactory.getLogger(PollingHttpClient.class);
    private static final int MILLIS_PER_SEC = 1000;
    private static final int HTTP_BAD_REQUEST = 400;

    private DefaultHttpClient httpClient;
    private int maxWaitPeriodInMilliSeconds;

    public static final int ERROR_NOT_EXPECTED = -1;

    public PollingHttpClient() {
        this(new DefaultHttpClient(), 60);
    }

    public PollingHttpClient(DefaultHttpClient httpClient, int waitPeriodInSeconds) {
        this.httpClient = httpClient;
        this.maxWaitPeriodInMilliSeconds = waitPeriodInSeconds * MILLIS_PER_SEC;
    }

    public HttpResponse get(String uri) throws IOException, InterruptedException {
        return get(uri, new DefaultResponseHandler());
    }

    public HttpResponse execute(HttpUriRequest request) throws IOException, InterruptedException {
        return execute(request, new DefaultResponseHandler(), ERROR_NOT_EXPECTED);
    }

    public HttpResponse execute(HttpUriRequest request, int expectedErrorCode) throws IOException, InterruptedException {
        return execute(request, new DefaultResponseHandler(), expectedErrorCode);
    }

    public <T> T get(String uri, final ResponseHandler<? extends T> responseHandler) throws IOException, InterruptedException {
        return execute(new HttpGet(uri), responseHandler, ERROR_NOT_EXPECTED);
    }

    public <T> T execute(HttpUriRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException, InterruptedException {
        return execute(request, responseHandler, ERROR_NOT_EXPECTED);
    }

    public <T> T execute(HttpUriRequest request, final ResponseHandler<? extends T> responseHandler,
                         int expectedErrorCode) throws IOException, InterruptedException {
        return executeWithWaitForUriAvailability(request, responseHandler, expectedErrorCode);
    }

    private <T> T executeWithWaitForUriAvailability(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler,
                                                    int expectedErrorCode) throws InterruptedException, IOException {
        HttpResponse response = null;
        long startTime = System.currentTimeMillis();
        long waitingFor;
        try {
            do {
                try {
                    if (response != null) {
                        EntityUtils.consume(response.getEntity());
                    }

                    response = httpClient.execute(httpUriRequest);

                    if (responseNotFound(response, expectedErrorCode)) {
                        LOG.warn("Response not found. Thread stopped for 2 seconds.");
                        Thread.sleep(2 * MILLIS_PER_SEC);
                    }
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                    Thread.sleep(2 * MILLIS_PER_SEC);
                }

                waitingFor = System.currentTimeMillis() - startTime;
            } while (responseNotFound(response, expectedErrorCode) && waitingFor < maxWaitPeriodInMilliSeconds);

            return response == null ? null : responseHandler.handleResponse(response);
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }

    public CredentialsProvider getCredentialsProvider() {
        return httpClient.getCredentialsProvider();
    }

    private static boolean responseNotFound(HttpResponse response, int expectedErrorCode) {
        if (response == null) {
            return true;
        }

        int statusCode = response.getStatusLine().getStatusCode();

        return statusCode > HTTP_BAD_REQUEST && statusCode != expectedErrorCode;
    }

    private class DefaultResponseHandler implements ResponseHandler<HttpResponse> {

        @Override
        public HttpResponse handleResponse(HttpResponse response) throws IOException {
            return response;
        }

    }

    public void setCookieStore(CookieStore cookieStore) {
        httpClient.setCookieStore(cookieStore);
    }
}







