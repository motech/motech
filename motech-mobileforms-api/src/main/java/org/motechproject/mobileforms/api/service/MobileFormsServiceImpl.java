package org.motechproject.mobileforms.api.service;

import org.motechproject.mobileforms.api.dao.AllMobileForms;
import org.motechproject.mobileforms.api.domain.Form;
import org.motechproject.mobileforms.api.domain.FormGroup;
import org.motechproject.mobileforms.api.valueobjects.GroupNameAndForms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Component
public class MobileFormsServiceImpl implements MobileFormsService {

    private AllMobileForms allMobileForms;

    @Autowired
    public MobileFormsServiceImpl(AllMobileForms allMobileForms) {
        this.allMobileForms = allMobileForms;
    }

    @Override
    public List<Object[]> getAllFormGroups() {
        return extractStudyNamesWithIndex(allMobileForms.getAllFormGroups());
    }

    @Override
    public GroupNameAndForms getForms(Integer formGroupIndex) {
        FormGroup formGroup = allMobileForms.getGroup(formGroupIndex);
        return new GroupNameAndForms(formGroup.getName(), extract(formGroup.getForms(), on(Form.class).getContent()));
    }

    private List<Object[]> extractStudyNamesWithIndex(List<FormGroup> allFormGroups) {
        List<String> studyNames = extract(allFormGroups, on(FormGroup.class).getName());

        List<Object[]> studyNamesWithIndex = new ArrayList<Object[]>();
        for(int i=0; i < studyNames.size(); i++){
            studyNamesWithIndex.add(new Object[]{i, studyNames.get(i)});
        }
        return studyNamesWithIndex;
    }

}
