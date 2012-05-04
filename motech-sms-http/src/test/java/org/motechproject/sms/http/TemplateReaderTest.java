package org.motechproject.sms.http;

import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.springframework.test.util.ReflectionTestUtils.getField;

public class TemplateReaderTest {

    @Test
    public void shouldReadFromTemplate() {
        TemplateReader templateReader = new TemplateReader();
        SmsHttpTemplate smsHttpTemplate = templateReader.getTemplate("/sample-template.json");

        SmsHttpTemplate.Request request = smsHttpTemplate.getOutgoing().getRequest();
        assertEquals("http://smshost.com/sms/send", (String) getField(request, "urlPath"));

        Map<String, String> queryParameters = (Map<String, String>) getField(request, "queryParameters");
        assertEquals("$message", queryParameters.get("message"));

        SmsHttpTemplate.Response response = smsHttpTemplate.getOutgoing().getResponse();
        assertEquals("sent", (String) getField(response, "success"));

        SmsHttpTemplate.Incoming incoming = smsHttpTemplate.getIncoming();
        assertEquals("$sender",incoming.getSenderKey());
        assertEquals("$message",incoming.getMessageKey());
        assertEquals("$time",incoming.getTimestampKey());
    }

}
