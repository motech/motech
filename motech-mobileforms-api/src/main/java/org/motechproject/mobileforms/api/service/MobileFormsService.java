package org.motechproject.mobileforms.api.service;

import org.motechproject.mobileforms.api.valueobjects.GroupNameAndForms;

import java.util.List;
import java.util.Map;

public interface MobileFormsService {
    List<Object[]> getAllFormGroups();
    GroupNameAndForms getForms(Integer formGroupIndex);
    Map<Integer,String> getAllForms();
}
