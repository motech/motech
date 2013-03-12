package org.motechproject.testing.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PollingHttpClient {
    private static Logger logger = Logger.getLogger(PollingHttpClient.class.getName());

    private static final int MILLIS_PER_SEC = 1000;
    private static final int CONNECT_TIMEOUT = 30 * 1000;

    private int maxWaitPeriodInMilliSeconds;
    private final DefaultHttpClient httpClient;


    public PollingHttpClient(DefaultHttpClient httpClient, int waitPeriodInSeconds) {
        this.httpClient = httpClient;
        this.maxWaitPeriodInMilliSeconds = waitPeriodInSeconds * MILLIS_PER_SEC;
    }

    public PollingHttpClient() {
        this(new DefaultHttpClient(), 30);
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
        waitForUriAvailability(getRequestUrl(httpUriRequest));
        return httpClient.execute(httpUriRequest, responseHandler);
    }

    private String getRequestUrl(HttpUriRequest request) {
        return request.getRequestLine().getUri();
    }


    private void waitForUriAvailability(String uri) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        int responseCode = HttpStatus.SC_NOT_FOUND;
        HttpURLConnection connection = null;
        long waitingFor = 0;
        do {
            try {
                connection = (HttpURLConnection) new URL(uri).openConnection();
                connection.setConnectTimeout(CONNECT_TIMEOUT);
                connection.connect();
                responseCode = connection.getResponseCode();
                if (responseNotFound(responseCode)) {
                    Thread.sleep(2 * MILLIS_PER_SEC);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage());
                Thread.sleep(2 * MILLIS_PER_SEC);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            waitingFor = System.currentTimeMillis() - startTime;

        } while (responseNotFound(responseCode) && waitingFor < maxWaitPeriodInMilliSeconds);
    }

    public CredentialsProvider getCredentialsProvider() {
        return httpClient.getCredentialsProvider();
    }

    private static boolean responseNotFound(int responseCode) {
        return responseCode == HttpStatus.SC_NOT_FOUND;
    }

    private class DefaultResponseHandler implements ResponseHandler<HttpResponse> {

        @Override
        public HttpResponse handleResponse(HttpResponse response) throws IOException {
            return response;
        }

    }
}







