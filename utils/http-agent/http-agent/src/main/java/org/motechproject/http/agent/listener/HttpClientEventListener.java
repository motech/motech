package org.motechproject.http.agent.listener;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.http.HttpException;
import org.apache.log4j.Logger;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.http.agent.domain.EventDataKeys;
import org.motechproject.http.agent.domain.EventSubjects;
import org.motechproject.http.agent.service.Method;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpClientEventListener {

    public static final String HTTP_CONNECT_TIMEOUT = "http.connect.timeout";
    public static final String HTTP_READ_TIMEOUT = "http.read.timeout";
    public static final int HUNDRED = 100;

    private Logger logger = Logger.getLogger(HttpClientEventListener.class);

    private RestTemplate restTemplate;

    @Autowired
    public HttpClientEventListener(RestTemplate restTemplate,
            @Qualifier("httpAgentSettings") SettingsFacade settings) {
        HttpComponentsClientHttpRequestFactory requestFactory = (HttpComponentsClientHttpRequestFactory) restTemplate
                .getRequestFactory();
        requestFactory.setConnectTimeout(Integer.parseInt(settings
                .getProperty(HTTP_CONNECT_TIMEOUT)));
        requestFactory.setReadTimeout(Integer.parseInt(settings
                .getProperty(HTTP_READ_TIMEOUT)));
        this.restTemplate = restTemplate;
    }

    @MotechListener(subjects = EventSubjects.HTTP_REQUEST)
    public void handle(MotechEvent motechEvent) {
        Map<String, Object> parameters = motechEvent.getParameters();
        String url = String.valueOf(parameters.get(EventDataKeys.URL));
        Object requestData = parameters.get(EventDataKeys.DATA);
        Method method = (Method) parameters.get(EventDataKeys.METHOD);
        logger.info(String.format("Posting Http request -- Url: %s, Data: %s",
                url, String.valueOf(requestData)));
        executeFor(url, requestData, method);
    }

    @MotechListener(subjects = EventSubjects.SYNC_HTTP_REQUEST_RET_TYPE)
    public ResponseEntity<?> handleWithReturnType(MotechEvent motechEvent) {
        Map<String, Object> parameters = motechEvent.getParameters();
        final String url = String.valueOf(parameters.get(EventDataKeys.URL));
        final Object requestData = parameters.get(EventDataKeys.DATA);
        final Method method = (Method) parameters.get(EventDataKeys.METHOD);
        int retryCount = 1; // default retry count = 1
        long retryInterval = 0; // by default, no waiting time between two
                                // retries
        if (EventDataKeys.RETRY_COUNT != null) {
            retryCount = (int) parameters.get(EventDataKeys.RETRY_COUNT);
        }
        if (EventDataKeys.RETRY_INTERVAL != null) {
            retryInterval = (long) parameters.get(EventDataKeys.RETRY_INTERVAL);
        }
        ResponseEntity<?> retValue = null;

        // defining a callable which attempts retry if response is not 2xx
        Callable<ResponseEntity<?>> task = new Callable<ResponseEntity<?>>() {
            public ResponseEntity<?> call() throws HttpException {

                try {
                    logger.info(String.format(
                            "Posting Http request -- Url: %s, Data: %s", url,
                            String.valueOf(requestData)));
                    ResponseEntity<?> response = executeForReturnType(url,
                            requestData, method);
                    if (response.getStatusCode().value() / HUNDRED == 2) {
                        return response;
                    } else {
                        throw new HttpException();
                    }
                } catch (HttpException e) {
                    throw e;
                }
            }
        };

        // Creating new instance of RetriableTask to run the callable
        RetriableTask<ResponseEntity<?>> r = new RetriableTask<ResponseEntity<?>>(
                retryCount, retryInterval, task);
        try {
            retValue = r.call();
        } catch (Exception e) { // Http request failed for all retries
            logger.info(String
                    .format("Posting Http request -- Url: %s, Data: %s failed after %s retries at interval of %s ms.",
                            url, String.valueOf(requestData),
                            String.valueOf(retryCount),
                            String.valueOf(retryInterval)));
        }
        return retValue;

    }

    private void executeFor(String url, Object requestData, Method method) {
        method.execute(restTemplate, url, requestData);
    }

    private ResponseEntity<?> executeForReturnType(String url,
            Object requestData, Method method) {
        return method.executeWithReturnType(restTemplate, url, requestData);
    }
}
