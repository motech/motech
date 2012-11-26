package org.motechproject.commcare.web;

import org.motechproject.commcare.domain.SettingsDto;
import org.motechproject.server.config.SettingsFacade;
import org.osgi.framework.BundleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Controller
public class SettingsController {
    private static final String COMMCARE_BASE_URL_KEY = "commcareBaseUrl";
    private static final String COMMCARE_DOMAIN_KEY = "commcareDomain";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String CASE_EVENT_STRATEGY_KEY = "case.events.send.with.all.data";

    private SettingsFacade settingsFacade;

    @Autowired
    public SettingsController(final SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    @ResponseBody
    public SettingsDto getSettings() {
        SettingsDto dto = new SettingsDto();
        dto.setCommcareBaseUrl(getPropertyValue(COMMCARE_BASE_URL_KEY));
        dto.setCommcareDomain(getPropertyValue(COMMCARE_DOMAIN_KEY));
        dto.setUsername(getPropertyValue(USERNAME_KEY));
        dto.setPassword(getPropertyValue(PASSWORD_KEY));
        dto.setEventStrategy(getPropertyValue(CASE_EVENT_STRATEGY_KEY));

        return dto;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    public void saveSettings(@RequestBody SettingsDto settings, @RequestParam(required = false, defaultValue = "false") boolean restart) throws BundleException {
        if (settings.isValid()) {
            settingsFacade.setProperty(COMMCARE_BASE_URL_KEY, settings.getCommcareBaseUrl());
            settingsFacade.setProperty(COMMCARE_DOMAIN_KEY, settings.getCommcareDomain());
            settingsFacade.setProperty(USERNAME_KEY, settings.getUsername());
            settingsFacade.setProperty(PASSWORD_KEY, settings.getPassword());
            settingsFacade.setProperty(CASE_EVENT_STRATEGY_KEY, settings.getEventStrategy());

            if (restart) {
                //OsgiListener.getOsgiService().restart(settingsFacade.getSymbolicName());
            }
        } else {
            throw new IllegalArgumentException("Settings are not valid");
        }
    }

    private String getPropertyValue(final String propertyKey) {
        String propertyValue = settingsFacade.getProperty(propertyKey);
        return isNotBlank(propertyValue) ? propertyValue : null;
    }

}
