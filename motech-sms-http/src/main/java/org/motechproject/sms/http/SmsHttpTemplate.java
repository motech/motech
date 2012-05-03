package org.motechproject.sms.http;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SmsHttpTemplate {

    public static class Outgoing {
        private Request request;
        private Response response;

        public Request getRequest() {
            return request;
        }

        public void setRequest(Request request) {
            this.request = request;
        }

        public Response getResponse() {
            return response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }
    }

    public static class Request {
        private String urlPath;
        private String recipientsSeparator;
        private Map<String, String> queryParameters;

        public String getUrlPath() {
            return urlPath;
        }

        public void setUrlPath(String urlPath) {
            this.urlPath = urlPath;
        }

        public String getRecipientsSeparator() {
            return recipientsSeparator;
        }

        public void setRecipientsSeparator(String recipientsSeparator) {
            this.recipientsSeparator = recipientsSeparator;
        }

        public Map<String, String> getQueryParameters() {
            return queryParameters;
        }

        public void setQueryParameters(Map<String, String> queryParameters) {
            this.queryParameters = queryParameters;
        }
    }

    public static class Response {
        private String success;

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }
    }

    public static class Incoming {
        private String messageKey;
        private String senderKey;
        private String timestampKey;

        public String getMessageKey() {
            return messageKey;
        }

        public void setMessageKey(String messageKey) {
            this.messageKey = messageKey;
        }

        public String getSenderKey() {
            return senderKey;
        }

        public void setSenderKey(String senderKey) {
            this.senderKey = senderKey;
        }

        public String getTimestampKey() {
            return timestampKey;
        }

        public void setTimestampKey(String timestampKey) {
            this.timestampKey = timestampKey;
        }
    }

    public static final String MESSAGE_PLACEHOLDER = "$message";
    public static final String RECIPIENTS_PLACEHOLDER = "$recipients";

    private Outgoing outgoing;
    private Incoming incoming;

    public HttpMethod generateRequestFor(List<String> recipients, String message) {
        GetMethod getMethod = new GetMethod(outgoing.getRequest().urlPath);

        List<NameValuePair> queryStringValues = new ArrayList<NameValuePair>();
        for (String key : outgoing.getRequest().queryParameters.keySet()) {
            String value = placeHolderOrLiteral(outgoing.getRequest().queryParameters.get(key), recipients, message);
            queryStringValues.add(new NameValuePair(key, value));
        }
        getMethod.setQueryString(queryStringValues.toArray(new NameValuePair[queryStringValues.size()]));
        return getMethod;
    }

    private String placeHolderOrLiteral(String value, List<String> recipients, String message) {
        if (value.equals(MESSAGE_PLACEHOLDER))
            return message;
        if (value.equals(RECIPIENTS_PLACEHOLDER))
            return StringUtils.join(recipients.iterator(), outgoing.getRequest().recipientsSeparator);
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
}
