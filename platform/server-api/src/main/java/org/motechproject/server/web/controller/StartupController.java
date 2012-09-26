package org.motechproject.server.web.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.motechproject.server.config.settings.MotechSettings;
import org.motechproject.server.osgi.OsgiFrameworkService;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.motechproject.server.web.validator.StartupFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;


@Controller
public class StartupController {
    private static final String COUCH_DB_URL = "http://localhost:5984";
    private static final String ACTIVE_MQ_URL = "tcp://localhost:61616";
    private static final String SCHEDULER_URL = "";

    private static final String START_PARAM = "START";

    private static final String ADMIN_SYMBOLIC_NAME = "org.motechproject.motech-admin-bundle";

    private StartupManager startupManager = StartupManager.getInstance();

    @Autowired
    private PlatformSettingsService platformSettingsService;

    @Autowired
    private OsgiFrameworkService osgiFrameworkService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new StartupFormValidator());
    }

    @RequestMapping(value = "/startup", method = RequestMethod.GET)
    public ModelAndView startup(final HttpServletRequest request) {
        ModelAndView view = new ModelAndView("startup");

        if (startupManager.canLaunchBundles()) {
            view.setViewName("redirect:home");
        } else {
            Locale locale = RequestContextUtils.getLocale(request);

            StartupForm startupSettings = new StartupForm();
            startupSettings.setLanguage(locale.getLanguage());

            view.addObject("suggestions", createSuggestions());
            view.addObject("startupSettings", startupSettings);
            view.addObject("languages", getLanguages());
        }

        return view;
    }

    @RequestMapping(value = "/startup", method = RequestMethod.POST)
    public ModelAndView submitForm(@RequestParam(value = START_PARAM, required = false) String start,
                                   @ModelAttribute("startupSettings") @Valid StartupForm form,
                                   BindingResult result) {
        ModelAndView view = new ModelAndView();

        if (result.hasErrors()) {
            view.addObject("suggestions", createSuggestions());
            view.addObject("languages", getLanguages());
            view.setViewName("startup");
        } else {
            ConfigFileSettings settings = startupManager.getLoadedConfig();
            settings.setProperty(MotechSettings.LANGUAGE, form.getLanguage());
            settings.setProperty(MotechSettings.AMQ_BROKER_URL, form.getQueueUrl());
            settings.setProperty(MotechSettings.SCHEDULER_URL, form.getSchedulerUrl());
            settings.setProperty(MotechSettings.DB_HOST, form.getDatabaseHost());
            settings.setProperty(MotechSettings.DB_PORT, form.getDatabasePort());

            platformSettingsService.savePlatformSettings(settings);
            startupManager.startup();

            if (startupManager.canLaunchBundles()) {
                if (StringUtils.isNotBlank(start)) {
                    osgiFrameworkService.startMotechBundles();
                    view.setViewName("redirect:home");
                } else {
                    if (osgiFrameworkService.startBundle(ADMIN_SYMBOLIC_NAME)) {
                        view.setViewName("redirect:module/admin/#/settings");
                    }
                }
            }
        }

        return view;
    }

    private StartupSuggestionsForm createSuggestions() {
        MotechSettings settings = startupManager.getLoadedConfig();
        Properties couchDBProperties = settings.getCouchDBProperties();
        StartupSuggestionsForm suggestions = new StartupSuggestionsForm();

        suggestions.addDatabaseSuggestion(String.format("http://%s:%s", couchDBProperties.getProperty("host"), couchDBProperties.getProperty("port")));
        suggestions.addQueueSuggestion(settings.getActivemqProperties().getProperty(MotechSettings.AMQ_BROKER_URL));
        suggestions.addSchedulerSuggestion(settings.getSchedulerProperties().getProperty(MotechSettings.SCHEDULER_URL));

        if (startupManager.findCouchDBInstance(COUCH_DB_URL)) {
            suggestions.addDatabaseSuggestion(COUCH_DB_URL);
        }

        if (startupManager.findActiveMQInstance(ACTIVE_MQ_URL)) {
            suggestions.addQueueSuggestion(ACTIVE_MQ_URL);
        }

        if (startupManager.findSchedulerInstance(SCHEDULER_URL)) {
            suggestions.addSchedulerSuggestion(SCHEDULER_URL);
        }

        return suggestions;
    }

    private List<String> getLanguages() {
        return Arrays.asList("en", "pl", "fr", "de");
    }

}
