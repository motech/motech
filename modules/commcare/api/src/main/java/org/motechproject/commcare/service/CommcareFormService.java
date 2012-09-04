package org.motechproject.commcare.service;

import org.motechproject.commcare.domain.CommcareForm;

public interface CommcareFormService {

    CommcareForm retrieveForm(String id);

    String retrieveFormJson(String id);
}
