package org.motechproject.mds.web.controller;

import org.motechproject.mds.constants.MdsRolesConstants;
import org.motechproject.mds.dto.MdsSettingsDto;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * The <code>SettingsController</code> is the Spring Framework Controller used by view layer for
 * executing certain actions on module settings.
 */
@Controller
public class SettingsController {

    @Autowired
    @Qualifier("mdsSettings")
    private SettingsFacade settingsFacade;

    @RequestMapping(value = "/settings/importFile", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(MdsRolesConstants.HAS_SETTINGS_ACCESS)
    public void importData(@RequestBody Object file) {

    }

    @RequestMapping(value = "/settings/exportData", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(MdsRolesConstants.HAS_SETTINGS_ACCESS)
    public void export(@RequestBody Object exportTable) {

    }

    @RequestMapping(value = "/settings/saveSettings", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(MdsRolesConstants.HAS_SETTINGS_ACCESS)
    public void saveSettings(@RequestBody MdsSettingsDto settings) {
        settingsFacade.saveConfigProperties(MdsSettingsDto.MDS_PROPERTIES_FILE_NAME, settings.toProperties());
    }

    @RequestMapping(value = "/settings/get", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize(MdsRolesConstants.HAS_SETTINGS_ACCESS)
    public MdsSettingsDto getSettings() {
        return new MdsSettingsDto(settingsFacade.getProperties(MdsSettingsDto.MDS_PROPERTIES_FILE_NAME));
    }
}
