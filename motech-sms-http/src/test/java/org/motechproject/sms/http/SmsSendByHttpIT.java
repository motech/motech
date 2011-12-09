package org.motechproject.sms.http;

import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.model.MotechEvent;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static org.motechproject.sms.api.service.SmsService.*;

public class SmsSendByHttpIT {

    @Test
    @Ignore ("only for end-to-end with production rancard server and david's number/kookoo api-key")
    public void shouldSendSmsThroughRancard() throws IOException {
        SmsSendHandler handler = new SmsSendHandler(new TemplateReader());
        MotechEvent event = new MotechEvent(SEND_SMS, new HashMap<String, Object>() {{
            put(RECIPIENTS, Arrays.asList("968****"));
            put(MESSAGE, "You should see spaces.");
        }});
        handler.handle(event);
    }
}
