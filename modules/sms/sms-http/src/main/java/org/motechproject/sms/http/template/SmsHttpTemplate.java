package org.motechproject.sms.http.template;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.motechproject.sms.http.domain.HttpMethodType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SmsHttpTemplate {
    public static final String MESSAGE_PLACEHOLDER = "$message";
    public static final String RECIPIENTS_PLACEHOLDER = "$recipients";

    private Outgoing outgoing;
    private Incoming incoming;

    public HttpMethod generateRequestFor(List<String> recipients, String message) {
        HttpMethod httpMethod;
        if (HttpMethodType.POST.equals(outgoing.getRequest().getType())) {
            httpMethod = new PostMethod(outgoing.getRequest().getUrlPath());
            addBodyParameters((PostMethod) httpMethod, recipients, message);
        } else {
            httpMethod = new GetMethod(outgoing.getRequest().getUrlPath());
        }

        httpMethod.setQueryString(addQueryParameters(recipients, message));
        return httpMethod;
    }

    private NameValuePair[] addQueryParameters(List<String> recipients, String message) {
        List<NameValuePair> queryStringValues = new ArrayList<NameValuePair>();
        Map<String, String> queryParameters = outgoing.getRequest().getQueryParameters();
        for (String key : queryParameters.keySet()) {
            String value = placeHolderOrLiteral(queryParameters.get(key), recipients, message);
            queryStringValues.add(new NameValuePair(key, value));
        }
        return queryStringValues.toArray(new NameValuePair[queryStringValues.size()]);
    }

    private void addBodyParameters(PostMethod postMethod, List<String> recipients, String message) {
        Map<String, String> bodyParameters = outgoing.getRequest().getBodyParameters();
        for (String key : bodyParameters.keySet()) {
            String value = placeHolderOrLiteral(bodyParameters.get(key), recipients, message);
            postMethod.setParameter(key, value);
        }
    }

    private String placeHolderOrLiteral(String value, List<String> recipients, String message) {
        if (value.equals(MESSAGE_PLACEHOLDER)) {
            return message;
        }
        if (value.equals(RECIPIENTS_PLACEHOLDER)) {
            return StringUtils.join(recipients.iterator(), outgoing.getRequest().getRecipientsSeparator());
        }
        return value;
    }

    public Outgoing getOutgoing() {
        return outgoing;
    }

    public void setOutgoing(Outgoing outgoing) {
        this.outgoing = outgoing;
    }

    public Incoming getIncoming() {
        return incoming;
    }

    public void setIncoming(Incoming incoming) {
        this.incoming = incoming;
    }

    public Authentication getAuthentication() {
        return outgoing.getRequest().getAuthentication();
    }

    public String getResponseSuccessCode() {
        return outgoing.getResponse().getSuccess();
    }
}
