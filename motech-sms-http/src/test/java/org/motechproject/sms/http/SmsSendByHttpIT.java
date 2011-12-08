package org.motechproject.sms.http;

import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.api.EventKeys;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static org.springframework.test.util.ReflectionTestUtils.setField;

public class SmsSendByHttpIT {

    @Test
    @Ignore ("only for end-to-end with production rancard server and david's number/kookoo api-key")
    public void shouldSendSmsThroughRancard() throws IOException {
        SmsSendHandler handler = new SmsSendHandler(new TemplateReader());
        MotechEvent event = new MotechEvent(EventKeys.SEND_SMS, new HashMap<String, Object>() {{
            put(EventKeys.RECIPIENTS, Arrays.asList("968****"));
            put(EventKeys.MESSAGE, "You should see spaces.");
        }});
        handler.handle(event);
    }
}
