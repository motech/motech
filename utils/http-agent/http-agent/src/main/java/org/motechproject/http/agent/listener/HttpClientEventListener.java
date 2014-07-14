package org.motechproject.http.agent.listener;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.http.agent.domain.EventDataKeys;
import org.motechproject.http.agent.domain.EventSubjects;
import org.motechproject.http.agent.domain.Method;
import org.motechproject.http.agent.factory.HttpComponentsClientHttpRequestFactoryWithAuth;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class HttpClientEventListener {

    public static final String HTTP_CONNECT_TIMEOUT = "http.connect.timeout";
    public static final String HTTP_READ_TIMEOUT = "http.read.timeout";

    private Logger logger = Logger.getLogger(HttpClientEventListener.class);

    private RestTemplate basicRestTemplate;
    private SettingsFacade settings;


    @Autowired
    public HttpClientEventListener(RestTemplate basicRestTemplate,
                                   @Qualifier("httpAgentSettings") SettingsFacade settings) {
        HttpComponentsClientHttpRequestFactory requestFactory =
                (HttpComponentsClientHttpRequestFactory) basicRestTemplate.getRequestFactory();
        requestFactory.setConnectTimeout(Integer.parseInt(settings.getProperty(HTTP_CONNECT_TIMEOUT)));
        requestFactory.setReadTimeout(Integer.parseInt(settings.getProperty(HTTP_READ_TIMEOUT)));

        this.basicRestTemplate = basicRestTemplate;
        this.settings = settings;
    }

    @MotechListener(subjects = EventSubjects.HTTP_REQUEST)
    public void handle(MotechEvent motechEvent) {
        Map<String, Object> parameters = motechEvent.getParameters();
        String url = String.valueOf(parameters.get(EventDataKeys.URL));
        Object requestData = parameters.get(EventDataKeys.DATA);
        String username = (String) parameters.get(EventDataKeys.USERNAME);
        String password = (String) parameters.get(EventDataKeys.PASSWORD);

        Object methodObj = parameters.get(EventDataKeys.METHOD);
        Method method = (methodObj instanceof Method) ? (Method) parameters.get(EventDataKeys.METHOD) :
                Method.fromString((String) methodObj);

        Map<String, String> headers = (Map<String, String>) parameters.get(EventDataKeys.HEADERS);
        if (headers == null) {
            headers = new HashMap<>();
        }
        HttpEntity<Object> entity = new HttpEntity<>(requestData, createHttpHeaders(headers));

        logger.info(String.format("Posting Http request -- Url: %s, Data: %s",
                url, String.valueOf(requestData)));

        executeFor(url, entity, method, username, password);
    }

    private void executeFor(String url,  HttpEntity<Object> requestData, Method method, String username, String password) {
        RestTemplate restTemplate;

        if (StringUtils.isNotBlank(username) || StringUtils.isNotBlank(password)) {
            restTemplate = new RestTemplate(usernamePasswordRequestFactory(username, password));
        } else {
            restTemplate = basicRestTemplate;
        }

        method.execute(restTemplate, url, requestData);
    }

    private HttpComponentsClientHttpRequestFactoryWithAuth usernamePasswordRequestFactory(String username, String password) {
        HttpComponentsClientHttpRequestFactoryWithAuth requestFactory =
                new HttpComponentsClientHttpRequestFactoryWithAuth(username, password);

        requestFactory.setConnectTimeout(Integer.parseInt(settings.getProperty(HTTP_CONNECT_TIMEOUT)));
        requestFactory.setReadTimeout(Integer.parseInt(settings.getProperty(HTTP_READ_TIMEOUT)));

        return requestFactory;
    }

    private HttpHeaders createHttpHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (String param : headers.keySet()){
            httpHeaders.add(param, headers.get(param));
        }

        return httpHeaders;
    }
}
