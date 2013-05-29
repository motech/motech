package org.motechproject.sms.http;


import com.google.gson.reflect.TypeToken;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.http.template.SmsHttpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TemplateReader {

    private String templateFileName;

    private SettingsFacade settings;

    @Autowired
    public void setSettings(@Qualifier("smsHttpSettings") SettingsFacade settings) {
        this.settings = settings;
    }

    public TemplateReader() {
        this.templateFileName = "sms-http-template.json";
    }

    public TemplateReader(String templateFileName) {
        this.templateFileName = templateFileName;
    }

    public SmsHttpTemplate getTemplate() {
        return (SmsHttpTemplate) new MotechJsonReader().readFromStream(settings.getRawConfig(templateFileName),
                new TypeToken<SmsHttpTemplate>() { }
                 .getType());
    }
}
