package org.motechproject.sms.http;

import org.apache.commons.httpclient.URIException;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class SmsHttpTemplateTest {

    @Test
    public void shouldGenerateRequestUrl() throws URIException {
        SmsHttpTemplate.Request request = new SmsHttpTemplate.Request();
        request.setUrlPath("http://smshost.com/sms/send");
        Map<String, String> queryParameters = new HashMap<String, String>() {{
            put("to", "sucker");
        }};
        request.setQueryParameters(queryParameters);

        SmsHttpTemplate smsHttpTemplate = new SmsHttpTemplate();
        SmsHttpTemplate.Outgoing outgoing = new SmsHttpTemplate.Outgoing();
        outgoing.setRequest(request);
        smsHttpTemplate.setOutgoing(outgoing);

        assertEquals("http://smshost.com/sms/send?to=sucker", smsHttpTemplate.generateRequestFor(null, null).getURI().getURI());
    }

    @Test
    public void shouldReplaceMessageVariableWithValue() throws URIException {
        SmsHttpTemplate.Request request = new SmsHttpTemplate.Request();
        request.setUrlPath("http://smshost.com/sms/send");
        Map<String, String> queryParameters = new HashMap<String, String>() {{
            put("message", "$message");
        }};
        request.setQueryParameters(queryParameters);

        SmsHttpTemplate smsHttpTemplate = new SmsHttpTemplate();
        SmsHttpTemplate.Outgoing outgoing = new SmsHttpTemplate.Outgoing();
        outgoing.setRequest(request);
        smsHttpTemplate.setOutgoing(outgoing);

        assertEquals("http://smshost.com/sms/send?message=foobar", smsHttpTemplate.generateRequestFor(null, "foobar").getURI().getURI());
    }

    @Test
    public void shouldReplaceReciepientsVariableWithValue() throws URIException {
        SmsHttpTemplate.Request request = new SmsHttpTemplate.Request();
        request.setUrlPath("http://smshost.com/sms/send");
        request.setRecipientsSeparator(",");
        Map<String, String> queryParameters = new HashMap<String, String>() {{
            put("recipients", "$recipients");
        }};
        request.setQueryParameters(queryParameters);

        SmsHttpTemplate smsHttpTemplate = new SmsHttpTemplate();
        SmsHttpTemplate.Outgoing outgoing = new SmsHttpTemplate.Outgoing();
        outgoing.setRequest(request);
        smsHttpTemplate.setOutgoing(outgoing);

        assertEquals("http://smshost.com/sms/send?recipients=123,456,789", smsHttpTemplate.generateRequestFor(Arrays.asList("123", "456", "789"), null).getURI().getURI());
    }
}
