package org.motechproject.mobileforms.api.repository;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.google.gson.reflect.TypeToken;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.mobileforms.api.domain.Form;
import org.motechproject.mobileforms.api.domain.FormGroup;
import org.motechproject.mobileforms.api.utils.IOUtils;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

import static ch.lambdaj.Lambda.convert;

@Repository
public class AllMobileForms {

    public static final String FORMS_CONFIG_FILE = "forms-config.json";

    private SettingsFacade settings;
    private MotechJsonReader motechJsonReader;
    private IOUtils ioUtils;
    private List<FormGroup> formGroups;

    AllMobileForms(@Qualifier("mobileFormsSettings") SettingsFacade settings, MotechJsonReader motechJsonReader, IOUtils ioUtils) {
        this.settings = settings;
        this.motechJsonReader = motechJsonReader;
        this.ioUtils = ioUtils;
        initialize();
    }

    @Autowired
    public AllMobileForms(@Qualifier("mobileFormsSettings") SettingsFacade settings) {
        this(settings, new MotechJsonReader(), new IOUtils());
    }

    public void initialize() {
        List<FormGroup> formGroupsFromConfigFile = (List<FormGroup>) motechJsonReader.readFromStream(
            settings.getRawConfig(FORMS_CONFIG_FILE),
            new TypeToken<List<FormGroup>>() { } .getType()
        );

        this.formGroups = convert(formGroupsFromConfigFile, new Converter<FormGroup, FormGroup>() {
            @Override
            public FormGroup convert(final FormGroup formGroup) {
                return new FormGroup(formGroup.getName(),
                        Lambda.convert(formGroup.getForms(),
                                new Converter<Form, Form>() {
                                    @Override
                                    public Form convert(Form form) {
                                        return new Form(
                                                form.name(),
                                                form.fileName(),
                                                ioUtils.getFileContent(form.fileName(), formGroup.getName()),
                                                form.bean(),
                                                form.validator(),
                                                formGroup.getName(),
                                                form.getDepends());
                                    }
                                }));
            }
        });
    }

    public List<FormGroup> getAllFormGroups() {
        return formGroups;
    }

    public FormGroup getFormGroup(Integer index) {
        return formGroups.get(index);
    }

    public Form getFormByName(String formName) {
        for (FormGroup formGroup : formGroups) {
            for (Form form : formGroup.getForms()) {
                if (form.name().equalsIgnoreCase(formName)) {
                    return form;
                }
            }
        }
        return null;
    }
}
