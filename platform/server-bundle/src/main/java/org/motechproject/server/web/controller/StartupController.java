package org.motechproject.server.web.controller;

import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.domain.ConfigFileSettings;
import org.motechproject.server.config.settings.MotechSettings;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleService;
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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.motechproject.security.helper.AuthenticationMode.REPOSITORY;
import static org.motechproject.server.config.settings.MotechSettings.AMQ_BROKER_URL;
import static org.motechproject.server.config.settings.MotechSettings.LANGUAGE;
import static org.motechproject.server.config.settings.MotechSettings.LOGINMODE;
import static org.motechproject.server.config.settings.MotechSettings.PROVIDER_NAME;
import static org.motechproject.server.config.settings.MotechSettings.PROVIDER_URL;
import static org.motechproject.server.config.settings.MotechSettings.SCHEDULER_URL;

/*StartupController manage register process */
@Controller
public class StartupController {

    public static final String BUNDLE_ADMIN_ROLE = "Bundle Admin";
    public static final String USER_ADMIN_ROLE = "User Admin";

    private StartupManager startupManager = StartupManager.getInstance();

    @Autowired
    private PlatformSettingsService platformSettingsService;

    @Autowired
    private LocaleService localeService;

    @Autowired
    private MotechUserService userService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new StartupFormValidator(userService));
    }

    @RequestMapping(value = "/startup", method = RequestMethod.GET)
    public ModelAndView startup(final HttpServletRequest request) {
        ModelAndView view = new ModelAndView("startup");

        if (startupManager.canLaunchBundles()) {
            view.setViewName("redirect:home");
        } else {
            Locale userLocale = localeService.getUserLocale(request);

            StartupForm startupSettings = new StartupForm();
            startupSettings.setLanguage(userLocale.getLanguage());

            view.addObject("suggestions", createSuggestions());
            view.addObject("startupSettings", startupSettings);
            view.addObject("languages", localeService.getAvailableLanguages());
            view.addObject("pageLang", userLocale);
        }

        return view;
    }

    @RequestMapping(value = "/startup", method = RequestMethod.POST)
    public ModelAndView submitForm(@ModelAttribute("startupSettings") @Valid StartupForm form,
                                   BindingResult result) {
        ModelAndView view = new ModelAndView("redirect:home");

        if (result.hasErrors()) {
            view.addObject("suggestions", createSuggestions());
            view.addObject("languages", localeService.getAvailableLanguages());
            view.addObject("loginMode", form.getLoginMode());
            view.addObject("errors", getErrors(result));

            view.setViewName("startup");
        } else {
            ConfigFileSettings settings = startupManager.getLoadedConfig();
            settings.saveMotechSetting(LANGUAGE, form.getLanguage());
            settings.saveMotechSetting(SCHEDULER_URL, form.getSchedulerUrl());
            settings.saveMotechSetting(LOGINMODE, form.getLoginMode());
            settings.saveActiveMqSetting(AMQ_BROKER_URL, form.getQueueUrl());
            settings.saveMotechSetting(PROVIDER_NAME, form.getProviderName());
            settings.saveMotechSetting(PROVIDER_URL, form.getProviderUrl());

            platformSettingsService.savePlatformSettings(settings.getMotechSettings());
            platformSettingsService.saveActiveMqSettings(settings.getActivemqProperties());

            if (REPOSITORY.equals(form.getLoginMode())) {
                registerAdminUser(form);
            }

            startupManager.startup();
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
        StartupSuggestionsForm suggestions = new StartupSuggestionsForm();

        String queueUrl = settings.getActivemqProperties().getProperty(AMQ_BROKER_URL);
        String schedulerUrl = settings.getSchedulerProperties().getProperty(SCHEDULER_URL);

        if (startupManager.findActiveMQInstance(queueUrl)) {
            suggestions.addQueueSuggestion(queueUrl);
        }

        if (startupManager.findSchedulerInstance(schedulerUrl)) {
            suggestions.addSchedulerSuggestion(schedulerUrl);
        }

        return suggestions;
    }

    private void registerAdminUser(StartupForm form) {
        String login = form.getAdminLogin();
        String password = form.getAdminPassword();
        String email = form.getAdminEmail();
        Locale locale = new Locale(form.getLanguage());

        List<String> roles = Arrays.asList(USER_ADMIN_ROLE, BUNDLE_ADMIN_ROLE);

        userService.register(login, password, email, null, roles, locale);
    }
}
