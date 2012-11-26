package org.motechproject.openmrs.ws;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;

import org.motechproject.commons.api.MotechException;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

/**
 * Represents a single OpenMRS Web Application instance
 */
@Component
public class OpenMrsInstance {

    private static final String OPENMRS_MOTECH_ID_NAME_PROPERTY = "openmrs.motechIdName";
    private static final String OPENMRS_URL_PROPERTY = "openmrs.url";
    private static final String OPENMRS_WEB_SERVICE_PATH = "/ws/rest/v1";

    private String openmrsUrl;
    private String motechPatientIdentifierTypeName;

    private SettingsFacade settingsFacade;

    @Autowired
    public OpenMrsInstance(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    @PostConstruct
    public void readSettings() {
        this.openmrsUrl = settingsFacade.getProperty(OPENMRS_URL_PROPERTY) + OPENMRS_WEB_SERVICE_PATH;
        this.motechPatientIdentifierTypeName = settingsFacade.getProperty(OPENMRS_MOTECH_ID_NAME_PROPERTY);
    }

    public String getOpenmrsUrl() {
        return openmrsUrl;
    }

    public String getMotechPatientIdentifierTypeName() {
        return motechPatientIdentifierTypeName;
    }

    public URI toInstancePath(String path) {
        try {
            return new URI(openmrsUrl + path);
        } catch (URISyntaxException e) {
            throw new MotechException("Bad URI", e);
        }
    }

    public URI toInstancePathWithParams(String path, Object... params) {
        return new UriTemplate(openmrsUrl + path).expand(params);
    }
}
