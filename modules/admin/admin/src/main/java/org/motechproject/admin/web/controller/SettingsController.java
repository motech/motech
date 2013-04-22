package org.motechproject.admin.web.controller;

import org.motechproject.admin.service.SettingsService;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.admin.settings.Settings;
import org.motechproject.admin.settings.SettingsOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class SettingsController {

    private static final Logger LOG = LoggerFactory.getLogger(SettingsController.class);

    private static final String PLATFORM_SETTINGS_SAVED = "{settings.saved}";
    private static final String ADMIN_MODULE_NAME = "admin";

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private StatusMessageService statusMessageService;

    @RequestMapping(value = "/settings/{bundleId}", method = RequestMethod.GET)
    @ResponseBody public List<Settings> getBundleSettings(@PathVariable long bundleId) throws IOException {
        return settingsService.getBundleSettings(bundleId);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/{bundleId}", method = RequestMethod.POST)
    public void saveBundleSettings(@PathVariable long bundleId, @RequestBody Settings bundleSettings) throws IOException {
        settingsService.saveBundleSettings(bundleSettings, bundleId);
        statusMessageService.info("{settings.saved.bundle}", ADMIN_MODULE_NAME);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/platform/list", method = RequestMethod.POST)
    public void savePlatformSettings(@RequestBody Settings[] platformSettings) {
        settingsService.savePlatformSettings(Arrays.asList(platformSettings));
        statusMessageService.info("{settings.saved.bundle}", ADMIN_MODULE_NAME);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/platform/new", method = RequestMethod.POST)
    public void saveNewSettings(HttpServletRequest request) {
        List<SettingsOption> settingsOpts = constructSettingsOptions(request);
        Settings settings = new Settings("couchdb", settingsOpts);
        settingsService.savePlatformSettings(settings);
    }

    @RequestMapping(value = "/settings/platform", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void savePlatformSettings(@RequestBody Settings platformSettings) throws IOException {
        settingsService.savePlatformSettings(platformSettings);
        statusMessageService.info(PLATFORM_SETTINGS_SAVED, ADMIN_MODULE_NAME);
    }

    @RequestMapping(value = "/settings/platform", method = RequestMethod.GET)
    @ResponseBody public List<Settings> getPlatformSettings() {
        return settingsService.getSettings();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/platform/upload", method = RequestMethod.POST)
    public void uploadSettingsFile(@RequestParam(required = true) MultipartFile settingsFile) {
        settingsService.saveSettingsFile(settingsFile);
        statusMessageService.info(PLATFORM_SETTINGS_SAVED, ADMIN_MODULE_NAME);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/platform/activemq/upload", method = RequestMethod.POST)
    public void uploadActiveMqFIle(@RequestParam(required = true) MultipartFile activemqFile) {
        settingsService.saveActiveMqFile(activemqFile);
        statusMessageService.info(PLATFORM_SETTINGS_SAVED, ADMIN_MODULE_NAME);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/platform/location", method = RequestMethod.POST)
    public void uploadSettingsLocation(@RequestParam(required = true) String location) throws IOException {
        settingsService.addSettingsPath(location);
        statusMessageService.info("{settings.saved.location}", ADMIN_MODULE_NAME);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/bundles/list", method = RequestMethod.GET)
    @ResponseBody public List<String> getBundlesWithSettings() {
        return settingsService.retrieveRegisteredBundleNames();
    }

    @RequestMapping(value = "/settings/{bundleId}/raw", method = RequestMethod.GET)
    @ResponseBody public List<String> getRawFilenames(@PathVariable long bundleId) {
        return settingsService.getRawFilenames(bundleId);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/{bundleId}/raw", method = RequestMethod.POST)
    void uploadRawFile(@PathVariable long bundleId, @RequestParam(required = true) String filename,
                       @RequestParam(required = true) MultipartFile file) {
        settingsService.saveRawFile(file, filename, bundleId);
        statusMessageService.info("{settings.saved.file}", ADMIN_MODULE_NAME);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public void handleException(Exception e) {
        LOG.error(e.getMessage(), e);
        statusMessageService.error("Error: " + e.getMessage(), ADMIN_MODULE_NAME);
    }

    private static List<SettingsOption> constructSettingsOptions(HttpServletRequest request) {
        List<SettingsOption> settingsOptions = new ArrayList<>();
        Map<Object, Object> paramMap = request.getParameterMap();

        for (Map.Entry<Object, Object> param : paramMap.entrySet()) {
            SettingsOption option = new SettingsOption();

            String[] value = (String[]) param.getValue();
            option.setValue(value[0]);
            option.setKey((String) param.getKey());

            settingsOptions.add(option);
        }
        return settingsOptions;
    }
}
