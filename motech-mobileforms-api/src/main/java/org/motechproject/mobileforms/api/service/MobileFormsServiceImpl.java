package org.motechproject.mobileforms.api.service;

import org.motechproject.mobileforms.api.dao.AllMobileForms;
import org.motechproject.mobileforms.api.domain.FormGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MobileFormsServiceImpl implements MobileFormsService {

    private AllMobileForms allMobileForms;

    @Autowired
    public MobileFormsServiceImpl(AllMobileForms allMobileForms) {
        this.allMobileForms = allMobileForms;
    }

    @Override
    public List<String> getAllFormGroups() {
        List<FormGroup> formGroups = allMobileForms.getAllFormGroups();
        List<String> formGroupNames = new ArrayList<String>();
        for(FormGroup group : formGroups) {
            formGroupNames.add(group.getName());
        }
        return formGroupNames;
    }
}
