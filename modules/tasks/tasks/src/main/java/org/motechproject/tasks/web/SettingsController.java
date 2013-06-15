package org.motechproject.tasks.web;

import org.motechproject.server.config.SettingsFacade;
import org.motechproject.tasks.domain.SettingsDto;
import org.osgi.framework.BundleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Controller
public class SettingsController {
    private static final String TASK_POSSIBLE_ERRORS = "task.possible.errors";

    private SettingsFacade settingsFacade;

    @Autowired
    public SettingsController(final SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    @ResponseBody
    public SettingsDto getSettings() {
        SettingsDto dto = new SettingsDto();
        dto.setTaskPossibleErrors(getPropertyValue(TASK_POSSIBLE_ERRORS));
        return dto;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    public void saveSettings(@RequestBody SettingsDto settings) throws BundleException {
        if (settings.isValid()) {
            settingsFacade.setProperty(TASK_POSSIBLE_ERRORS, settings.getTaskPossibleErrors());
        } else {
            throw new IllegalArgumentException("Settings are not valid");
        }
    }

    private String getPropertyValue(final String propertyKey) {
        String propertyValue = settingsFacade.getProperty(propertyKey);
        return isNotBlank(propertyValue) ? propertyValue : null;
    }

}
