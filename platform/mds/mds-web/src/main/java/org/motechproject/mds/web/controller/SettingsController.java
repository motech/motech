package org.motechproject.mds.web.controller;

import org.motechproject.mds.config.ModuleSettings;
import org.motechproject.mds.config.SettingsService;
import org.motechproject.mds.domain.ImportExportBlueprint;
import org.motechproject.mds.service.ImportExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.CharEncoding.UTF_8;
import static org.motechproject.mds.util.Constants.Roles;

/**
 * The <code>SettingsController</code> is the Spring Framework Controller used by view layer for
 * executing certain actions on module settings.
 */
@Controller("mdsSettingsController")
public class SettingsController {
    private static final String INCLUDE_SCHEMA = "schema";
    private static final String INCLUDE_DATA = "data";

    private SettingsService settingsService;
    private ImportExportService importExportService;

    @RequestMapping(value = "/settings/importFile", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Roles.HAS_SETTINGS_ACCESS)
    public void importData(@RequestBody Object file) {

    }

    @RequestMapping(value = "/settings/export", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Roles.HAS_SETTINGS_ACCESS)
    public void export(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        ImportExportBlueprint blueprint = getBlueprint(request.getParameterMap());

        response.setContentType("application/json");
        response.setCharacterEncoding(UTF_8);
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=mds.json");

        importExportService.exportEntities(blueprint, response.getWriter());
    }

    private ImportExportBlueprint getBlueprint(Map requestParameterMap) {
        ImportExportBlueprint blueprint = new ImportExportBlueprint();

        for (Object parameterEntryObject : requestParameterMap.entrySet()) {
            Map.Entry parameterEntry = (Map.Entry) parameterEntryObject;
            String entity = parameterEntry.getKey() instanceof String ? (String) parameterEntry.getKey() : null;
            List<String> include = parameterEntry.getValue() instanceof String[] ? Arrays.asList((String[]) parameterEntry.getValue()) : null;
            if (null != entity && null != include) {
                if (include.contains(INCLUDE_SCHEMA)) {
                    blueprint.includeEntitySchema(entity);
                }
                if (include.contains(INCLUDE_DATA)) {
                    blueprint.includeEntityData(entity);
                }
            }
        }

        return blueprint;
    }

    @RequestMapping(value = "/settings/saveSettings", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Roles.HAS_SETTINGS_ACCESS)
    public void saveSettings(@RequestBody ModuleSettings settings) {
        settingsService.saveModuleSettings(settings);
    }

    @RequestMapping(value = "/settings/get", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize(Roles.HAS_SETTINGS_ACCESS)
    public ModuleSettings getSettings() {
        return settingsService.getModuleSettings();
    }

    @Autowired
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Autowired
    public void setImportExportService(ImportExportService importExportService) {
        this.importExportService = importExportService;
    }
}
