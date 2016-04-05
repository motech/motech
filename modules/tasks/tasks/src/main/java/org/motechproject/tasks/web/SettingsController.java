package org.motechproject.tasks.web;

import org.motechproject.config.SettingsFacade;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.web.domain.SettingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Controller for managing Tasks module settings.
 */
@PreAuthorize(TasksRoles.HAS_ROLE_MANAGE_TASKS)
@Controller
public class SettingsController {

    private static final String TASK_POSSIBLE_ERRORS = "task.possible.errors";

    @Autowired
    private SettingsFacade settingsFacade;

    /**
     * Returns the Tasks module settings.
     *
     * @return  the settings of Task module
     */
    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    @ResponseBody
    public SettingsDto getSettings() {
        SettingsDto dto = new SettingsDto();
        dto.setTaskPossibleErrors(getPropertyValue(TASK_POSSIBLE_ERRORS));
        return dto;
    }

    /**
     * Saves the given settings.
     *
     * @param settings  the settings to be saved, not null
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    public void saveSettings(@RequestBody SettingsDto settings) {
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
