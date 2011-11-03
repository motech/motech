package org.motechproject.mobileforms.api.repository;

import ch.lambdaj.function.convert.Converter;
import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.mobileforms.api.domain.Form;
import org.motechproject.mobileforms.api.domain.FormGroup;
import org.motechproject.mobileforms.api.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;

import static ch.lambdaj.Lambda.convert;

@Repository
public class AllMobileForms {
    public static final String FORMS_CONFIG_FILE = "forms.config.file";
    private Properties properties;
    private MotechJsonReader motechJsonReader;
    private IOUtils ioUtils;
    private List<FormGroup> formGroups;

    @Autowired
    public AllMobileForms(@Qualifier(value = "mobileFormsProperties") Properties properties, MotechJsonReader motechJsonReader, IOUtils ioUtils) {
        this.properties = properties;
        this.motechJsonReader = motechJsonReader;
        this.ioUtils = ioUtils;
    }

    @PostConstruct
    public void initialize(){
        List<FormGroup> formGroupsFromConfigFile = (List<FormGroup>) motechJsonReader.readFromFile(configFile(), new TypeToken<List<FormGroup>>(){}.getType());
        this.formGroups = convert(formGroupsFromConfigFile, new Converter<FormGroup, FormGroup>() {
            @Override
            public FormGroup convert(final FormGroup formGroup) {
                return new FormGroup(formGroup.getName(), ch.lambdaj.Lambda.convert(formGroup.getForms(), new Converter<Form, Form>() {
                    @Override
                    public Form convert(Form form) {
                        return new Form(form.name(), form.fileName(), ioUtils.getFileContent(form.fileName(), formGroup.getName()));
                    }
                }));
            }
        });
    }

    public List<FormGroup> getAllFormGroups() {
        return formGroups;
    }

    public FormGroup getGroup(Integer indexOfFormGroupList) {
        return formGroups.get(indexOfFormGroupList);
    }

    private String configFile() {
        return this.properties.getProperty(FORMS_CONFIG_FILE);
    }


}