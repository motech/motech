package org.motechproject.commcare;

import org.motechproject.commcare.domain.CommcareUser;
import org.motechproject.commcare.domain.CommcareFixture;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.service.impl.CommcareCaseServiceImpl;
import org.motechproject.commcare.service.impl.CommcareFixtureServiceImpl;
import org.motechproject.commcare.service.impl.CommcareFormServiceImpl;
import org.motechproject.commcare.service.impl.CommcareUserServiceImpl;
import org.motechproject.commons.api.AbstractDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.Map;
import java.util.List;
import java.util.Arrays;

public class CommcareDataProvider extends AbstractDataProvider {
    private static final String SUPPORT_FIELD = "id";

    private CommcareUserServiceImpl commcareUserService;
    private CommcareFixtureServiceImpl commcareFixtureService;
    private CommcareCaseServiceImpl commcareCaseService;
    private CommcareFormServiceImpl commcareFormService;

    @Autowired
    public void setCommcareUserService(CommcareUserServiceImpl commcareUserService) {
        this.commcareUserService = commcareUserService;
    }

    @Autowired
    public void setCommcareFixtureService(CommcareFixtureServiceImpl commcareFixtureService) {
        this.commcareFixtureService = commcareFixtureService;
    }

    @Autowired
    public void setCommcareCaseService(CommcareCaseServiceImpl commcareCaseService) {
        this.commcareCaseService = commcareCaseService;
    }

    @Autowired
    public void setCommcareFormService(CommcareFormServiceImpl commcareFormService) {
        this.commcareFormService = commcareFormService;
    }

    @Autowired
    public CommcareDataProvider(ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource("task-data-provider.json");

        if (resource != null) {
            setBody(resource);
        }
    }

    @Override
    public String getName() {
        return "Commcare";
    }

    @Override
    public Object lookup(String type, Map<String, String> lookupFields) {
        Object obj = null;

        if (supports(type) && lookupFields.containsKey(SUPPORT_FIELD)) {
            String id = lookupFields.get(SUPPORT_FIELD);

            try {
                Class<?> cls = getClassForType(type);

                if (CommcareUser.class.isAssignableFrom(cls)) {
                    obj = getUser(id);
                } else if (CommcareFixture.class.isAssignableFrom(cls)) {
                    obj = getFixture(id);
                } else if (CaseInfo.class.isAssignableFrom(cls)) {
                    obj = getCase(id);
                } else if (CommcareForm.class.isAssignableFrom(cls)) {
                    obj = getForm(id);
                }
            } catch (ClassNotFoundException e) {
                logError(e.getMessage(), e);
            }
        }

        return obj;
    }

    @Override
    public List<Class<?>> getSupportClasses() {
        return Arrays.asList(CommcareUser.class, CommcareFixture.class, CommcareForm.class, CaseInfo.class);
    }

    @Override
    public String getPackageRoot() {
        return "org.motechproject.commcare.domain";
    }

    private CommcareUser getUser(String id) {
        return commcareUserService.getCommcareUserById(id);
    }

    private CommcareFixture getFixture(String id) {
        return commcareFixtureService.getCommcareFixtureById(id);
    }

    private CaseInfo getCase(String id) {
        return commcareCaseService.getCaseByCaseId(id);
    }

    private CommcareForm getForm(String id) {
        return commcareFormService.retrieveForm(id);
    }
}