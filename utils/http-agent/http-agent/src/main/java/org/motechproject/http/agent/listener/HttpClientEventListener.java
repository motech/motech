package org.motechproject.http.agent.listener;


import org.apache.log4j.Logger;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.http.agent.domain.EventDataKeys;
import org.motechproject.http.agent.domain.EventSubjects;
import org.motechproject.http.agent.domain.Method;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class HttpClientEventListener {

    public static final String HTTP_CONNECT_TIMEOUT = "http.connect.timeout";
    public static final String HTTP_READ_TIMEOUT = "http.read.timeout";

    private Logger logger = Logger.getLogger(HttpClientEventListener.class);

    private RestTemplate restTemplate;

    @Autowired
    public HttpClientEventListener(RestTemplate restTemplate, @Qualifier("httpAgentSettings") SettingsFacade settings) {
        HttpComponentsClientHttpRequestFactory requestFactory = (HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory();
        requestFactory.setConnectTimeout(Integer.parseInt(settings.getProperty(HTTP_CONNECT_TIMEOUT)));
        requestFactory.setReadTimeout(Integer.parseInt(settings.getProperty(HTTP_READ_TIMEOUT)));
        this.restTemplate = restTemplate;
    }

    @MotechListener(subjects = EventSubjects.HTTP_REQUEST)
    public void handle(MotechEvent motechEvent) {
        Map<String, Object> parameters = motechEvent.getParameters();
        String url = String.valueOf(parameters.get(EventDataKeys.URL));
        Object requestData = parameters.get(EventDataKeys.DATA);
        Method method = (Method) parameters.get(EventDataKeys.METHOD);
        logger.info(String.format("Posting Http request -- Url: %s, Data: %s", url, String.valueOf(requestData)));
        executeFor(url, requestData, method);
    }

    private void executeFor(String url, Object requestData, Method method) {
        method.execute(restTemplate, url, requestData);
    }
}
