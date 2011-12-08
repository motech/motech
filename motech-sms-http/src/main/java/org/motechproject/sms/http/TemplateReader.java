package org.motechproject.sms.http;


import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.springframework.stereotype.Component;

@Component
public class TemplateReader {

    private SmsSendTemplate smsSendTemplate;

    public TemplateReader(String filename) {
        this.smsSendTemplate = (SmsSendTemplate) new MotechJsonReader().readFromFile(filename, new TypeToken<SmsSendTemplate>() {
        }.getType());
    }

    public SmsSendTemplate getTemplate() {
        return smsSendTemplate;
    }

}
