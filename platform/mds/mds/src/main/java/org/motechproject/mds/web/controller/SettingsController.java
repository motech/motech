package org.motechproject.mds.web.controller;

import org.motechproject.mds.config.ModuleSettings;
import org.motechproject.mds.config.SettingsWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.motechproject.mds.util.Constants.Roles;

/**
 * The <code>SettingsController</code> is the Spring Framework Controller used by view layer for
 * executing certain actions on module settings.
 */
@Controller("mdsSettingsController")
public class SettingsController {
    private SettingsWrapper settingsWrapper;

    @RequestMapping(value = "/settings/importFile", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Roles.HAS_SETTINGS_ACCESS)
    public void importData(@RequestBody Object file) {

    }

    @RequestMapping(value = "/settings/exportData", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Roles.HAS_SETTINGS_ACCESS)
    public void export(@RequestBody Object exportTable) {

    }

    @RequestMapping(value = "/settings/saveSettings", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Roles.HAS_SETTINGS_ACCESS)
    public void saveSettings(@RequestBody ModuleSettings settings) {
        settingsWrapper.saveModuleSettings(settings);
    }

    @RequestMapping(value = "/settings/get", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize(Roles.HAS_SETTINGS_ACCESS)
    public ModuleSettings getSettings() {
        return settingsWrapper.getModuleSettings();
    }

    @Autowired
    public void setSettingsWrapper(SettingsWrapper settingsWrapper) {
        this.settingsWrapper = settingsWrapper;
    }
}
