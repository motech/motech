package org.motechproject.admin.web.controller;

import org.motechproject.admin.service.SettingsService;
import org.motechproject.admin.settings.BundleSettings;
import org.motechproject.admin.settings.SettingsOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @RequestMapping(value = "/settings/{bundleId}", method = RequestMethod.GET)
    public @ResponseBody List<BundleSettings> getPlatformSettings(@PathVariable long bundleId) throws IOException {
        return settingsService.getBundleSettings(bundleId);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/{bundleId}", method = RequestMethod.POST)
    public void saveBundleSettings(@PathVariable long bundleId, HttpServletRequest request) throws IOException {
        List<SettingsOption> options = constructSettingsOptions(request);
        settingsService.saveBundleSettings(options, bundleId);
    }

    @RequestMapping(value = "/settings/platform", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void savePlatformSettings(HttpServletRequest request) throws IOException {
        List<SettingsOption> options = constructSettingsOptions(request);
        settingsService.savePlatformSettings(options);
    }

    @RequestMapping(value = "/settings/platform", method = RequestMethod.GET)
    public @ResponseBody List<SettingsOption> getPlatformSettings() {
        return settingsService.getSettings();
    }

    private static List<SettingsOption> constructSettingsOptions(HttpServletRequest request) {
        List<SettingsOption> settingsOptions = new ArrayList<>();
        Map<Object, Object> paramMap = request.getParameterMap();

        for (Map.Entry<Object, Object> param : paramMap.entrySet()) {
            SettingsOption option = new SettingsOption();

            String[] value = (String[]) param.getValue();
            option.setValue(value[0]);
            option.setKey((String)param.getKey());

            settingsOptions.add(option);
        }
        return settingsOptions;
    }
}
