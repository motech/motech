package org.motechproject.sms.http;

import org.junit.Test;
import org.motechproject.sms.http.template.Incoming;
import org.motechproject.sms.http.template.Request;
import org.motechproject.sms.http.template.Response;
import org.motechproject.sms.http.template.SmsHttpTemplate;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.springframework.test.util.ReflectionTestUtils.getField;

public class TemplateReaderTest {

    @Test
    public void shouldReadFromTemplate() {
        TemplateReader templateReader = new TemplateReader("/templates/sample-template.json");
        SmsHttpTemplate smsHttpTemplate = templateReader.getTemplate();

        Request request = smsHttpTemplate.getOutgoing().getRequest();
        assertEquals("http://smshost.com/sms/send", (String) getField(request, "urlPath"));

        Map<String, String> queryParameters = (Map<String, String>) getField(request, "queryParameters");
        assertEquals("$message", queryParameters.get("message"));

        Response response = smsHttpTemplate.getOutgoing().getResponse();
        assertEquals("sent", (String) getField(response, "success"));

        Incoming incoming = smsHttpTemplate.getIncoming();
        assertEquals("$sender",incoming.getSenderKey());
        assertEquals("$message",incoming.getMessageKey());
        assertEquals("$time",incoming.getTimestampKey());
    }

}
