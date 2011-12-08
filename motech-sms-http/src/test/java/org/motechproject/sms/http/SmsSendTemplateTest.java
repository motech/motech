package org.motechproject.sms.http;

import org.apache.commons.httpclient.URIException;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class SmsSendTemplateTest {

    @Test
    public void shouldGenerateRequestUrl() throws URIException {
        SmsSendTemplate.Request request = new SmsSendTemplate.Request();
        ReflectionTestUtils.setField(request, "urlPath", "http://smshost.com/sms/send");
        Map<String, String> queryParameters = new HashMap<String, String>() {{
            put("to", "sucker");
        }};
        ReflectionTestUtils.setField(request, "queryParameters", queryParameters);
        SmsSendTemplate smsSendTemplate = new SmsSendTemplate();
        ReflectionTestUtils.setField(smsSendTemplate, "request", request);

        assertEquals("http://smshost.com/sms/send?to=sucker", smsSendTemplate.generateRequestFor(null, null).getURI().getURI());
    }
}
