package org.motechproject.mobileforms.api.service;

import org.motechproject.mobileforms.api.domain.FormGroup;

import java.util.List;
import java.util.Map;

public interface MobileFormsService {
    List<Object[]> getAllFormGroups();
    FormGroup getForms(Integer formGroupIndex);
    Map<Integer,String> getFormIdMap();
}
