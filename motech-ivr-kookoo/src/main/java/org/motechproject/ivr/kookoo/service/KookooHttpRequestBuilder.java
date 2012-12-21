package org.motechproject.ivr.kookoo.service;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.stereotype.Component;

@Component
public class KookooHttpRequestBuilder {

    public GetMethod newGetMethod(String url, NameValuePair[] queryParams) {
        GetMethod getMethod = new GetMethod(url);
        getMethod.setQueryString(queryParams);
        return getMethod;
    }

}
