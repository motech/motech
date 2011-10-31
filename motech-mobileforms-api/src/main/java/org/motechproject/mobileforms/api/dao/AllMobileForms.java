package org.motechproject.mobileforms.api.dao;

import ch.lambdaj.function.convert.Converter;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.motechproject.MotechException;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.mobileforms.api.domain.Form;
import org.motechproject.mobileforms.api.domain.FormGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static ch.lambdaj.Lambda.convert;

@Component
public class AllMobileForms {
    public static final String FORMS_CONFIG_FILE = "forms.config.file";
    public static final String XFORMS_FOLDER = "xforms";
    private Properties properties;
    private MotechJsonReader motechJsonReader;
    private List<FormGroup> formGroups;

    @Autowired
    public AllMobileForms(@Qualifier(value = "mobileFormsProperties") Properties properties, MotechJsonReader motechJsonReader) {
        this.properties = properties;
        this.motechJsonReader = motechJsonReader;
    }

    public void initialize(){
        List<FormGroup> formGroupsFromConfigFile = (List<FormGroup>) motechJsonReader.readFromFile(configFile(), new TypeToken<List<FormGroup>>(){}.getType());
        this.formGroups = convert(formGroupsFromConfigFile, new Converter<FormGroup, FormGroup>() {
            @Override
            public FormGroup convert(final FormGroup formGroup) {
                return new FormGroup(formGroup.getName(), ch.lambdaj.Lambda.convert(formGroup.getForms(), new Converter<Form, Form>() {
                    @Override
                    public Form convert(Form form) {
                        return new Form(form.getName(), form.getFileName(), getFileContent(form.getFileName(), formGroup.getName()));
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

    private String getFileContent(String fileName, String formGroupName) {
        List<String> paths = new ArrayList<String>();
        paths.add(XFORMS_FOLDER);
        paths.add(formGroupName);
        paths.add(fileName);
        String xformFilePath = StringUtils.collectionToDelimitedString(paths, File.separator);
        try {
            return FileUtils.readFileToString(new File(getClass().getClassLoader().getResource(xformFilePath).toURI().getPath()));
        } catch (Exception e) {
            throw new MotechException("Encountered error while loading openxdata forms", e);
        }
    }
}