package org.motechproject.commcare.web;

import org.apache.commons.validator.UrlValidator;
import org.motechproject.commcare.domain.SettingsDto;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.server.osgi.OsgiListener;
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

import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Controller
public class SettingsController {
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
        dto.setCommcareDomain(settingsFacade.getProperty(COMMCARE_DOMAIN_KEY));
        dto.setUsername(settingsFacade.getProperty(USERNAME_KEY));
        dto.setPassword(settingsFacade.getProperty(PASSWORD_KEY));
        dto.setEventStrategy(settingsFacade.getProperty(CASE_EVENT_STRATEGY_KEY));

        return dto;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    public void saveSettings(@RequestBody SettingsDto settings, @RequestParam(required = false, defaultValue = "false") boolean restart) throws BundleException {
        if (isValid(settings)) {
            settingsFacade.setProperty(COMMCARE_DOMAIN_KEY, settings.getCommcareDomain());
            settingsFacade.setProperty(USERNAME_KEY, settings.getUsername());
            settingsFacade.setProperty(PASSWORD_KEY, settings.getPassword());
            settingsFacade.setProperty(CASE_EVENT_STRATEGY_KEY, settings.getEventStrategy());

            if (restart) {
                OsgiListener.getOsgiService().restart(settingsFacade.getSymbolicName());
            }
        } else {
            throw new IllegalArgumentException("Settings are not valid");
        }
    }

    private boolean isValid(SettingsDto settings) {
        return isNotEmpty(settings.getCommcareDomain()) && isNotEmpty(settings.getUsername()) &&
                isNotEmpty(settings.getPassword()) && isNotEmpty(settings.getEventStrategy()) &&
                new UrlValidator().isValid(settings.getCommcareDomain().replace("localhost", "127.0.0.1"));
    }

}
