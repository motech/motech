package org.motechproject.sms.http;


import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.sms.http.template.SmsHttpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TemplateReader {

    @Value("#{smsHttpProperties['sms.http.configuration.filename']}")
    private String templateFileName;

    public TemplateReader() {
    }

    public TemplateReader(String templateFileName) {
        this.templateFileName = templateFileName;
    }

    public SmsHttpTemplate getTemplate() {
        return (SmsHttpTemplate) new MotechJsonReader().readFromStream(getClass().getResourceAsStream(templateFileName),
                new TypeToken<SmsHttpTemplate>() { }
                 .getType());
    }
}
