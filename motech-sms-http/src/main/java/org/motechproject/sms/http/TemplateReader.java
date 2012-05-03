package org.motechproject.sms.http;


import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.springframework.stereotype.Component;

@Component
public class TemplateReader {

    public SmsHttpTemplate getTemplate(String filename) {
        return (SmsHttpTemplate) new MotechJsonReader().readFromFile(filename, new TypeToken<SmsHttpTemplate>() {
        }.getType());
    }
}
