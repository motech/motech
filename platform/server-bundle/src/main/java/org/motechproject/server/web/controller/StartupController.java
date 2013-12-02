package org.motechproject.server.web.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.domain.LoginMode;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleService;
import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.motechproject.server.web.helper.SuggestionHelper;
import org.motechproject.server.web.validator.StartupFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import static org.motechproject.server.config.domain.MotechSettings.AMQ_BROKER_URL;
import static org.motechproject.server.web.controller.Constants.REDIRECT_HOME;

/**
 * StartupController that manages the platform system start up and captures the platform core settings and user information.
 */
@Controller
public class StartupController {

    public static final String BUNDLE_ADMIN_ROLE = "Bundle Admin";
    public static final String USER_ADMIN_ROLE = "User Admin";
    public static final String EMAIL_ADMIN_ROLE = "Email Admin";
    public static final String SECURITY_ADMIN_ROLE = "Security Admin";
    public static final String ROLES_ADMIN = "Roles Admin";

    @Autowired
    private StartupManager startupManager;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private LocaleService localeService;

    @Autowired
    private MotechUserService userService;

    @Autowired
    private SuggestionHelper suggestionHelper;

    @Autowired
    @Qualifier("mainHeaderStr")
    private String mainHeader;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new StartupFormValidator(userService, configurationService));
    }

    @RequestMapping(value = "/startup", method = RequestMethod.GET)
    public ModelAndView startup(final HttpServletRequest request) {
        ModelAndView view = new ModelAndView("startup");

        if (startupManager.canLaunchBundles()) {
            view.setViewName(REDIRECT_HOME);
        } else {
            Locale userLocale = localeService.getUserLocale(request);

            ConfigSource configSource = (configurationService.loadBootstrapConfig() != null) ?
                    configurationService.loadBootstrapConfig().getConfigSource() : ConfigSource.UI;

            StartupForm startupSettings = new StartupForm();
            startupSettings.setLanguage(userLocale.getLanguage());

            view.addObject("mainHeader", mainHeader);
            view.addObject("suggestions", createSuggestions());
            view.addObject("startupSettings", startupSettings);
            view.addObject("languages", localeService.getAvailableLanguages());
            view.addObject("pageLang", userLocale);
            view.addObject("isFileMode", ConfigSource.FILE.equals(configSource));
        }

        return view;
    }

    @RequestMapping(value = "/startup", method = RequestMethod.POST)
    public ModelAndView submitForm(@ModelAttribute("startupSettings") @Valid StartupForm form,
                                   BindingResult result) {
        ModelAndView view = new ModelAndView(REDIRECT_HOME);
        ConfigSource configSource = (configurationService.loadBootstrapConfig() != null) ?
                configurationService.loadBootstrapConfig().getConfigSource() : ConfigSource.UI;

        if (result.hasErrors()) {
            view.addObject("mainHeader", mainHeader);
            view.addObject("suggestions", createSuggestions());
            view.addObject("languages", localeService.getAvailableLanguages());
            view.addObject("loginMode", form.getLoginMode());
            view.addObject("errors", getErrors(result));
            view.addObject("isFileMode", ConfigSource.FILE.equals(configSource));

            view.setViewName("startup");
        } else {
            if (ConfigSource.UI.equals(configSource)) {
                MotechSettings settings = startupManager.getDefaultSettings();

                settings.setLanguage(form.getLanguage());
                settings.setLoginModeValue(form.getLoginMode());
                settings.savePlatformSetting(AMQ_BROKER_URL, form.getQueueUrl());
                settings.setProviderName(form.getProviderName());
                settings.setProviderUrl(form.getProviderUrl());

                configurationService.savePlatformSettings(settings);

                if (LoginMode.REPOSITORY.equals(LoginMode.valueOf(form.getLoginMode()))) {
                    registerAdminUser(form);
                }
            } else {
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
        StartupSuggestionsForm suggestions = new StartupSuggestionsForm();

        String queueUrl = suggestionHelper.suggestActivemqUrl();

        if (StringUtils.isNotBlank(queueUrl)) {
            suggestions.addQueueSuggestion(queueUrl);
        }

        return suggestions;
    }

    private void registerAdminUser(StartupForm form) {
        String login = form.getAdminLogin();
        String password = form.getAdminPassword();
        String email = form.getAdminEmail();
        Locale locale = new Locale(form.getLanguage());

        List<String> roles = Arrays.asList(USER_ADMIN_ROLE, BUNDLE_ADMIN_ROLE, EMAIL_ADMIN_ROLE, SECURITY_ADMIN_ROLE, ROLES_ADMIN);

        userService.register(login, password, email, null, roles, locale);
    }
}
