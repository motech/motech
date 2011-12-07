package org.motechproject.sms.http;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class SmsSendTemplate {

    static class Request {
        String urlPath;
        String phoneNumberSeparator;
        Map<String, String> queryParameters;
    }

    static class Response {
        String success;
        String failure;
    }

    Request request;
    Response response;

    public HttpMethod generateRequestFor(List<String> recipients, String message) {
        GetMethod getMethod = new GetMethod(request.urlPath);

        List<NameValuePair> queryStringValues = new ArrayList<NameValuePair>();
        for (String key : request.queryParameters.keySet())
            queryStringValues.add(new NameValuePair(key, variableOrLiteral(key)));
        getMethod.setQueryString(queryStringValues.toArray(new NameValuePair[queryStringValues.size()]));

        return getMethod;
    }

    private String variableOrLiteral(String key) {
        return request.queryParameters.get(key);
    }
}

