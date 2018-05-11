package org.motechproject.admin.web.controller;

import com.google.common.net.HttpHeaders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.motechproject.admin.settings.AdminSettings;
import org.motechproject.admin.internal.service.SettingsService;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.admin.settings.Settings;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Class responsible for communication between settings UI frontend and backend.
 */
@Controller
@Api(value="SettingsController", description = "Class responsible for communication between settings UI frontend and backend")
public class SettingsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsController.class);

    private static final String PLATFORM_SETTINGS_SAVED = "{admin.settings.saved}";
    private static final String ADMIN_MODULE_NAME = "admin";

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private StatusMessageService statusMessageService;

    /**
     * Returns setting options with their values for a bundle with the given id. Each {@link org.motechproject.admin.settings.Settings}
     * object describes one bundle configuration file.
     * @param bundleId the id of the bundle for which the settings will be retrieved
     * @return a list settings options for the bundle
     * @throws IOException if there was a problem reading the settings(if the config mode is FILE)
     */
    @RequestMapping(value = "/settings/{bundleId}", method = RequestMethod.GET)
    @ApiOperation(value="Returns setting options with their values for a bundle with the given id. Each {@link org.motechproject.admin.settings.Settings}\n" +
            "object describes one bundle configuration file")
    @ResponseBody public List<Settings> getBundleSettings(@PathVariable long bundleId) throws IOException {
        return settingsService.getBundleSettings(bundleId);
    }

    /**
     * Saves a settings section of a bundle.
     * @param bundleId the id of the bundle
     * @param bundleSettings the settings section to be saved
     * @throws IOException if there was a problem reading the settings (if the config mode is FILE)
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/{bundleId}", method = RequestMethod.POST)
    @ApiOperation(value="Saves a settings section of a bundle")
    public void saveBundleSettings(@PathVariable long bundleId, @RequestBody Settings bundleSettings) throws IOException {
        settingsService.saveBundleSettings(bundleSettings, bundleId);
        statusMessageService.info("{admin.settings.saved.bundle}", ADMIN_MODULE_NAME);
    }

    /**
     * Saves all platform settings.
     * @param platformSettings an array of {@link org.motechproject.admin.settings.Settings} objects representing
     *                         sections of the platform settings
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/platform/list", method = RequestMethod.POST)
    @ApiOperation(value="Saves all platform settings")
    public void savePlatformSettings(@RequestBody Settings[] platformSettings) {
        settingsService.savePlatformSettings(Arrays.asList(platformSettings));
        statusMessageService.info("{admin.settings.saved.bundle}", ADMIN_MODULE_NAME);
    }

    /**
     * Saves a particular section of the platform settings.
     * @param platformSettings the platform settings section to be saved
     * @throws IOException if there was a problem reading the settings (if the config mode is FILE)
     */
    @RequestMapping(value = "/settings/platform", method = RequestMethod.POST)
    @ApiOperation(value="Saves a particular section of the platform settings")
    @ResponseStatus(HttpStatus.OK)
    public void savePlatformSettings(@RequestBody Settings platformSettings) throws IOException {
        settingsService.savePlatformSettings(platformSettings);
        statusMessageService.info(PLATFORM_SETTINGS_SAVED, ADMIN_MODULE_NAME);
    }

    /**
     * Returns the setting for the platform.
     * @return the representation of all the platform settings
     * @see org.motechproject.admin.settings.AdminSettings
     */
    @RequestMapping(value = "/settings/platform", method = RequestMethod.GET)
    @ApiOperation(value="Returns the setting for the platform")
    @ResponseBody public AdminSettings getPlatformSettings() {
        return settingsService.getSettings();
    }

    /**
     * Exports the platform configuration in a zip file.
     * @param response the response to which the file will be written to
     * @throws IOException if there was a problem writing the zip file
     */
    @RequestMapping(value = "/settings/platform/export", method = RequestMethod.GET)
    @ApiOperation(value="Exports the platform configuration in a zip file")
    public void exportConfig(HttpServletResponse response) throws IOException {
        Date dateNow = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final String fileName = "config_" + dateFormat.format(dateNow) + ".zip";
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        response.addHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
        response.setContentType("application/octet-stream");

        try (InputStream is = settingsService.exportConfig(fileName)) {
            IOUtils.copy(is, response.getOutputStream());
        }

        response.getOutputStream().flush();
    }

    /**
     * Handles platform settings update through file upload.
     * @param file the representation of the file being uploaded
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/platform/upload", method = RequestMethod.POST)
    @ApiOperation(value="Handles platform settings update through file upload")
    public void uploadSettingsFile(@RequestParam(required = true) MultipartFile file) {
        settingsService.saveSettingsFile(file);
        statusMessageService.info(PLATFORM_SETTINGS_SAVED, ADMIN_MODULE_NAME);
    }

    /**
     * Handles adding a new configuration location to the system.
     * @param location the location to be added
     * @throws IOException if we were unable to add the location
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/platform/location", method = RequestMethod.POST)
    @ApiOperation(value="Handles adding a new configuration location to the system")
    public void uploadSettingsLocation(@RequestParam(required = true) String location) throws IOException {
        settingsService.addSettingsPath(location);
        statusMessageService.info("{settings.saved.location}", ADMIN_MODULE_NAME);
    }

    /**
     * Returns names of bundles with registered settings.
     * @return list of symbolic names for bundles with their settings registered
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/bundles/list", method = RequestMethod.GET)
    @ApiOperation(value="Returns names of bundles with registered settings")
    @ResponseBody public List<String> getBundlesWithSettings() {
        return settingsService.retrieveRegisteredBundleNames();
    }

    /**
     * Returns raw config file names for a given bundle. Raw config is for example a json file, that is displayed
     * to the user as an upload widget, instead a regular key-value settings UI.
     * @param bundleId the id of the bundle for which raw config should be retrieved
     * @return the list of raw config file names
     */
    @RequestMapping(value = "/settings/{bundleId}/raw", method = RequestMethod.GET)
    @ApiOperation(value="Returns raw config file names for a given bundle. Raw config is for example a json file, that is displayed\n" +
            "to the user as an upload widget, instead a regular key-value settings UI")
    @ResponseBody public List<String> getRawFilenames(@PathVariable long bundleId) {
        return settingsService.getRawFilenames(bundleId);
    }

    /**
     * Handles an upload of a raw configuration file for a bundle.
     * @param bundleId the id of the bundle which registers this file
     * @param filename the name of the file
     * @param file the representation of the file being uploaded
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/{bundleId}/raw", method = RequestMethod.POST)
    @ApiOperation(value="Handles an upload of a raw configuration file for a bundle")
    void uploadRawFile(@PathVariable long bundleId, @RequestParam(required = true) String filename,
                       @RequestParam(required = true) MultipartFile file) {
        settingsService.saveRawFile(file, filename, bundleId);
        statusMessageService.info("{admin.settings.saved.file}", ADMIN_MODULE_NAME);
    }

    /**
     * Handles exceptions thrown in the controller. Logs an error and publishes a status message in the system.
     * @param e the exception thrown
     * @see org.motechproject.admin.domain.StatusMessage
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public void handleException(Exception e) {
        LOGGER.error(e.getMessage(), e);
        statusMessageService.error("Error: " + e.getMessage(), ADMIN_MODULE_NAME);
    }
}
