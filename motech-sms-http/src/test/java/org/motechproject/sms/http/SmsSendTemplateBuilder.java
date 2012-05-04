package org.motechproject.sms.http;

import java.util.Map;

public class SmsSendTemplateBuilder {

    private SmsHttpTemplate smsHttpTemplate = new SmsHttpTemplate();

    public static class RequestBuilder {

        private SmsHttpTemplate.Request request = new SmsHttpTemplate.Request();

        public static RequestBuilder create() {
            return new RequestBuilder();
        }

        public RequestBuilder withUrlPath(String urlPath) {
            request.setUrlPath(urlPath);
            return this;
        }

        public RequestBuilder withQueryParameters(Map<String, String> queryParameters) {
            request.setQueryParameters(queryParameters);
            return this;
        }

        public SmsHttpTemplate.Request build() {
            return request;
        }
    }

    public static class ResponseBuilder {

        private SmsHttpTemplate.Response response = new SmsHttpTemplate.Response();

        public ResponseBuilder withSuccess(String success) {
            response.setSuccess(success);
            return this;
        }

        public static ResponseBuilder create() {
            return new ResponseBuilder();
        }

        public SmsHttpTemplate.Response build() {
            return response;
        }
    }

    public SmsSendTemplateBuilder withRequest(SmsHttpTemplate.Request request) {
        smsHttpTemplate.getOutgoing().setRequest(request);
        return this;
    }

    public SmsSendTemplateBuilder withResponse(SmsHttpTemplate.Response response) {
        smsHttpTemplate.getOutgoing().setResponse(response);
        return this;
    }

    public static SmsSendTemplateBuilder create() {
        return new SmsSendTemplateBuilder();
    }

    public SmsHttpTemplate build() {
        return smsHttpTemplate;
    }
}
