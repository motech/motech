package org.motechproject.testing.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PollingHttpClient {
    private static final Logger LOG = LoggerFactory.getLogger(PollingHttpClient.class);
    private static final int MILLIS_PER_SEC = 1000;

    private DefaultHttpClient httpClient;
    private int maxWaitPeriodInMilliSeconds;

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
        return execute(request, new DefaultResponseHandler());
    }


    public <T> T get(String uri, final ResponseHandler<? extends T> responseHandler) throws IOException, InterruptedException {
        return execute(new HttpGet(uri), responseHandler);
    }


    public <T> T execute(HttpUriRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException, InterruptedException {
        return executeOnUriAvailability(request, responseHandler);
    }

    private <T> T executeOnUriAvailability(HttpUriRequest httpUriRequest, final ResponseHandler<? extends T> responseHandler) throws IOException, InterruptedException {
        waitForUriAvailability(httpUriRequest);
        return httpClient.execute(httpUriRequest, responseHandler);
    }

    private void waitForUriAvailability(HttpUriRequest httpUriRequest) throws InterruptedException {
        HttpResponse response = null;
        long startTime = System.currentTimeMillis();
        long waitingFor;

        do {
            try {
                response = httpClient.execute(httpUriRequest);

                if (responseNotFound(response)) {
                    LOG.warn("Response not found. Thread stopped for 2 seconds.");
                    Thread.sleep(2 * MILLIS_PER_SEC);
                }

                EntityUtils.consume(response.getEntity());
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                Thread.sleep(2 * MILLIS_PER_SEC);
            }

            waitingFor = System.currentTimeMillis() - startTime;
        } while (responseNotFound(response) && waitingFor < maxWaitPeriodInMilliSeconds);
    }

    public CredentialsProvider getCredentialsProvider() {
        return httpClient.getCredentialsProvider();
    }

    private static boolean responseNotFound(HttpResponse response) {
        return response == null || response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND;
    }

    private class DefaultResponseHandler implements ResponseHandler<HttpResponse> {

        @Override
        public HttpResponse handleResponse(HttpResponse response) throws IOException {
            return response;
        }

    }
}







