package org.motechproject.mobileforms.api.dao;

import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.mobileforms.api.domain.FormGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class AllMobileForms {
    public static final String FORMS_CONFIG_FILE = "forms.config.file";
    private Properties properties;
    private MotechJsonReader motechJsonReader;

    @Autowired
    public AllMobileForms(@Qualifier(value = "mobileFormsProperties") Properties properties, MotechJsonReader motechJsonReader) {
        this.properties = properties;
        this.motechJsonReader = motechJsonReader;
    }

    public List<FormGroup> getAllFormGroups() {

        return (List<FormGroup>) motechJsonReader.readFromFile(configFile(),
                new TypeToken<List<FormGroup>>() {
                }.getType());
    }

    private String configFile() {
        return this.properties.getProperty(FORMS_CONFIG_FILE);

    }
}