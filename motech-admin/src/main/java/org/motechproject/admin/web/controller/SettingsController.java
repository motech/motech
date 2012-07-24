package org.motechproject.admin.web.controller;

import org.motechproject.admin.service.SettingsService;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.admin.settings.BundleSettings;
import org.motechproject.admin.settings.SettingsOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SettingsController {

    private static final String PLATFORM_SETTINGS_SAVED = "{settings.saved}";

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private StatusMessageService statusMessageService;

    @RequestMapping(value = "/settings/{bundleId}", method = RequestMethod.GET)
    public @ResponseBody List<BundleSettings> getBundleSettings(@PathVariable long bundleId) throws IOException {
        return settingsService.getBundleSettings(bundleId);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/{bundleId}", method = RequestMethod.POST)
    public void saveBundleSettings(@PathVariable long bundleId, HttpServletRequest request) throws IOException {
        List<SettingsOption> options = constructSettingsOptions(request);
        settingsService.saveBundleSettings(options, bundleId);
        statusMessageService.ok("{settings.saved.bundle}");
    }

    @RequestMapping(value = "/settings/platform", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void savePlatformSettings(HttpServletRequest request) throws IOException {
        List<SettingsOption> options = constructSettingsOptions(request);
        settingsService.savePlatformSettings(options);
        statusMessageService.ok(PLATFORM_SETTINGS_SAVED);
    }

    @RequestMapping(value = "/settings/platform", method = RequestMethod.GET)
    public @ResponseBody List<SettingsOption> getPlatformSettings() {
        return settingsService.getSettings();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/platform/upload", method = RequestMethod.POST)
    public void uploadSettingsFile(@RequestParam(required = true) MultipartFile settingsFile) {
        settingsService.saveSettingsFile(settingsFile);
        statusMessageService.ok(PLATFORM_SETTINGS_SAVED);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/platform/location", method = RequestMethod.POST)
    public void uploadSettingsLocation(@RequestParam(required = true) String location) {
        settingsService.addSettingsPath(location);
        statusMessageService.ok("{settings.saved.location}");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public void handleException(Exception e) {
        statusMessageService.error(e.getMessage());
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
