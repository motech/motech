package org.motechproject.http.agent.domain;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.client.RestTemplate;

public enum Method {
    POST {
        @Override
        public void execute(RestTemplate restTemplate, String url, Object request) {
            restTemplate.postForLocation(url, request);
        }
    },

    PUT {
        @Override
        public void execute(RestTemplate restTemplate, String url, Object request) {
            restTemplate.put(url, request);
        }
    },

    DELETE {
        @Override
        public void execute(RestTemplate restTemplate, String url, Object request) {
            restTemplate.delete(url, request);
        }
    };

    public abstract void execute(RestTemplate restTemplate, String url, Object request);

    public static Method fromString(String str) {
        for (Method method : values()) {
            if (StringUtils.equalsIgnoreCase(method.name(), str)) {
                return method;
            }
        }
        return POST;
    }
}
