package org.motechproject.sms.http;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class SmsSendTemplate {

    public static final String MESSAGE_PLACEHOLDER = "$message";
    public static final String RECIPIENTS_PLACEHOLDER = "$recipients";

    static class Request {
        String urlPath;
        String recipientsSeparator;
        Map<String, String> queryParameters;
    }

    Request request;

    public HttpMethod generateRequestFor(List<String> recipients, String message) {
        GetMethod getMethod = new GetMethod(request.urlPath);

        List<NameValuePair> queryStringValues = new ArrayList<NameValuePair>();
        for (String key : request.queryParameters.keySet())
            queryStringValues.add(new NameValuePair(key, variableOrLiteral(key, recipients, message)));
        getMethod.setQueryString(queryStringValues.toArray(new NameValuePair[queryStringValues.size()]));

        return getMethod;
    }

    private String variableOrLiteral(String s, List<String> recipients, String message) {
        String value = request.queryParameters.get(s);
        if (value == MESSAGE_PLACEHOLDER)
            return message;
        if (value == RECIPIENTS_PLACEHOLDER)
            return StringUtils.join(recipients.iterator(), request.recipientsSeparator);
        return value;
    }
}

