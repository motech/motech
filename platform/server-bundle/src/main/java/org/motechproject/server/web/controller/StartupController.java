package org.motechproject.server.web.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.motechproject.server.config.settings.MotechSettings;
import org.motechproject.server.osgi.OsgiListener;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleSettings;
import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.motechproject.server.web.validator.StartupFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;


@Controller
public class StartupController {
    private static final String START_PARAM = "START";

    private static final String ADMIN_SYMBOLIC_NAME = "org.motechproject.motech-admin-bundle";

    private StartupManager startupManager = StartupManager.getInstance();

    @Autowired
    private PlatformSettingsService platformSettingsService;

    @Autowired
    private LocaleSettings localeSettings;

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
            Locale userLocale = localeSettings.getUserLocale(request);

            StartupForm startupSettings = new StartupForm();
            startupSettings.setLanguage(userLocale.getLanguage());

            view.addObject("suggestions", createSuggestions());
            view.addObject("startupSettings", startupSettings);
            view.addObject("languages", localeSettings.getAvailableLanguages().keySet());
            view.addObject("pageLang", userLocale);
        }

        return view;
    }

    @RequestMapping(value = "/startup", method = RequestMethod.POST)
    public ModelAndView submitForm(@RequestParam(value = START_PARAM, required = false) String start,
                                   @ModelAttribute("startupSettings") @Valid StartupForm form,
                                   BindingResult result) {
        ModelAndView view = new ModelAndView("redirect:home");

        if (result.hasErrors()) {
            view.addObject("suggestions", createSuggestions());
            view.addObject("languages", localeSettings.getAvailableLanguages().keySet());
            view.addObject("errors", getErrors(result));

            view.setViewName("startup");
        } else {
            ConfigFileSettings settings = startupManager.getLoadedConfig();
            settings.saveMotechSetting(MotechSettings.LANGUAGE, form.getLanguage());
            settings.saveMotechSetting(MotechSettings.SCHEDULER_URL, form.getSchedulerUrl());
            settings.saveMotechSetting(MotechSettings.DB_HOST, form.getDatabaseHost());
            settings.saveMotechSetting(MotechSettings.DB_PORT, form.getDatabasePort());
            
            settings.saveActiveMqSetting(MotechSettings.AMQ_BROKER_URL, form.getQueueUrl());

            platformSettingsService.savePlatformSettings(settings.getMotechSettings());
            platformSettingsService.saveActiveMqSettings(settings.getActivemqProperties());
            startupManager.startup();

            if (startupManager.canLaunchBundles()) {
                if (StringUtils.isNotBlank(start)) {
                    OsgiListener.getOsgiService().startMotechBundles();
                } else {
                    OsgiListener.getOsgiService().startBundle(ADMIN_SYMBOLIC_NAME);
                }
            }
        }

        return view;
    }

    private List<String> getErrors(final BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        List<String> errors = new ArrayList<>(allErrors.size());

        for (ObjectError error : allErrors) {
            errors.add(error.getCode());
        }

        return errors;
    }

    private StartupSuggestionsForm createSuggestions() {
        MotechSettings settings = startupManager.getLoadedConfig();
        Properties couchDBProperties = settings.getCouchDBProperties();
        StartupSuggestionsForm suggestions = new StartupSuggestionsForm();

        String dbUrl = String.format("http://%s:%s", couchDBProperties.getProperty("host"), couchDBProperties.getProperty("port"));
        String queueUrl = settings.getActivemqProperties().getProperty(MotechSettings.AMQ_BROKER_URL);
        String schedulerUrl = settings.getSchedulerProperties().getProperty(MotechSettings.SCHEDULER_URL);

        if (startupManager.findCouchDBInstance(dbUrl)) {
            suggestions.addDatabaseSuggestion(dbUrl);
        }

        if (startupManager.findActiveMQInstance(queueUrl)) {
            suggestions.addQueueSuggestion(queueUrl);
        }

        if (startupManager.findSchedulerInstance(schedulerUrl)) {
            suggestions.addSchedulerSuggestion(schedulerUrl);
        }

        return suggestions;
    }

}