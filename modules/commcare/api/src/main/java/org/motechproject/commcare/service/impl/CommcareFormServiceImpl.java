package org.motechproject.commcare.service.impl;

import com.google.gson.JsonParseException;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.parser.FormAdapter;
import org.motechproject.commcare.service.CommcareFormService;
import org.motechproject.commcare.util.CommCareAPIHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommcareFormServiceImpl implements CommcareFormService {

    private CommCareAPIHttpClient commcareHttpClient;

    @Autowired
    public CommcareFormServiceImpl(CommCareAPIHttpClient commcareHttpClient) {
        this.commcareHttpClient = commcareHttpClient;
    }

    @Override
    public CommcareForm retrieveForm(String id) {
        String returnJson = commcareHttpClient.formRequest(id);

        try {
            return FormAdapter.readJson(returnJson);
        } catch (JsonParseException e) {
            return null;
        }
    }

    @Override
    public String retrieveFormJson(String id) {
        return commcareHttpClient.formRequest(id);
    }

}
