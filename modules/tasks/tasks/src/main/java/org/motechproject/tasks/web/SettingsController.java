package org.motechproject.tasks.web;

import org.motechproject.commons.api.MotechException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.motechproject.tasks.web.domain.SettingsDto.TASK_POSSIBLE_ERRORS;
import static org.motechproject.tasks.web.domain.SettingsDto.TASK_PROPERTIES_FILE_NAME;

/**
 * Controller for managing Tasks module settings.
 */
@PreAuthorize(TasksRoles.HAS_ROLE_MANAGE_TASKS)
@Controller
@Api(value="SettingsController", description = "Controller for managing Tasks module settings")
public class SettingsController {

    @Autowired
    private SettingsFacade settingsFacade;

    /**
     * Returns the Tasks module settings.
     *
     * @return  the settings of Task module
     */
    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    @ApiOperation(value="Returns the Tasks module settings")
    @ResponseBody
    public SettingsDto getSettings() {
        SettingsDto dto = new SettingsDto();
        dto.getProperties(dto, settingsFacade);
        return dto;
    }

    /**
     * Saves the given settings.
     *
     * @param settings  the settings to be saved, not null
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    @ApiOperation(value="Saves the given settings")
    public void saveSettings(@RequestBody SettingsDto settings) {
        String taskPossibleErrors = settings.getTaskPossibleErrors();

        try(OutputStream os = new ByteArrayOutputStream()) {
            settings.getTaskRetriesProps().store(os, "TaskRetries");

            if (settings.isValid()) {
                settingsFacade.setProperty(TASK_POSSIBLE_ERRORS, taskPossibleErrors);
                settingsFacade.saveRawConfig(TASK_PROPERTIES_FILE_NAME, new String(os.toString()));
            } else {
                throw new IllegalArgumentException("Settings are not valid");
            }
        } catch (IOException e) {
            throw new MotechException("Error parsing task retries", e);
        }
    }

}
