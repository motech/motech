package org.motechproject.server.web.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.domain.LoginMode;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleService;
import org.motechproject.server.web.dto.StartupViewData;
import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.motechproject.server.web.helper.SuggestionHelper;
import org.motechproject.server.web.validator.StartupFormValidator;
import org.motechproject.server.web.validator.StartupFormValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.motechproject.config.core.constants.ConfigurationConstants.AMQ_BROKER_URL;
import static org.motechproject.security.UserRoleNames.BUNDLE_ADMIN_ROLE;
import static org.motechproject.security.UserRoleNames.EMAIL_ADMIN_ROLE;
import static org.motechproject.security.UserRoleNames.MDS_ADMIN;
import static org.motechproject.security.UserRoleNames.ROLES_ADMIN;
import static org.motechproject.security.UserRoleNames.SECURITY_ADMIN_ROLE;
import static org.motechproject.security.UserRoleNames.USER_ADMIN_ROLE;
import static org.motechproject.server.web.controller.Constants.REDIRECT_HOME;

/**
 * StartupController that manages the platform system start up and captures the platform core settings and user information.
 */
@Controller
public class StartupController {

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

    @Autowired
    private StartupFormValidatorFactory startupFormValidatorFactory;

    @RequestMapping(value = "/startupviewdata", method = RequestMethod.GET)
    @ResponseBody
    public StartupViewData getStartupViewData(final HttpServletRequest request) {
        StartupViewData viewData = new StartupViewData();

        if (startupManager.canLaunchBundles()) {
            viewData.setRedirectHome(true);

            return viewData;
        } else {
            Locale userLocale = localeService.getUserLocale(request);

            ConfigSource configSource = (configurationService.loadBootstrapConfig() != null) ?
                    configurationService.loadBootstrapConfig().getConfigSource() : ConfigSource.UI;
            boolean requiresConfigFiles = configSource.isFile() && configurationService.requiresConfigurationFiles();

            StartupForm startupSettings = new StartupForm();
            startupSettings.setLanguage(userLocale.getLanguage());

            viewData.setRequireConfigFiles(requiresConfigFiles);
            viewData.setSuggestions(createSuggestions());
            viewData.setStartupSettings(startupSettings);
            viewData.setLanguages(localeService.getAvailableLanguages());
            viewData.setPageLang(userLocale);
            viewData.setIsFileMode(ConfigSource.FILE.equals(configSource));
            viewData.setIsAdminRegistered(userService.hasActiveAdminUser());
            viewData.setRedirectHome(false);
        }

        return viewData;
    }

    @RequestMapping(value = "/startup", method = RequestMethod.GET)
    public ModelAndView startup() {
        ModelAndView view = new ModelAndView("startup");

        if (startupManager.canLaunchBundles()) {
            view.setViewName(REDIRECT_HOME);
        } else {
            view.addObject("mainHeader", mainHeader);
        }

        return view;
    }

    @RequestMapping(value = "/startup", method = RequestMethod.POST)
    @ResponseBody
    public List<String> submitForm(@RequestBody StartupForm startupSettings) throws IOException {
        ConfigSource configSource = (configurationService.loadBootstrapConfig() != null) ?
                configurationService.loadBootstrapConfig().getConfigSource() : ConfigSource.UI;

        StartupFormValidator startupFormValidator = startupFormValidatorFactory.getStartupFormValidator(startupSettings, userService);
        List<String> errors = startupFormValidator.validate(startupSettings, configurationService.getConfigSource());

        if (!errors.isEmpty()) {
            return errors;
        } else if (!startupManager.canLaunchBundles()) {
            if (ConfigSource.UI.equals(configSource)) {
                MotechSettings settings = startupManager.getDefaultSettings();

                settings.setLanguage(startupSettings.getLanguage());
                settings.setLoginModeValue(startupSettings.getLoginMode());
                settings.savePlatformSetting(AMQ_BROKER_URL, startupSettings.getQueueUrl());
                settings.setProviderName(startupSettings.getProviderName());
                settings.setProviderUrl(startupSettings.getProviderUrl());

                configurationService.savePlatformSettings(settings);

                if (LoginMode.REPOSITORY.equals(LoginMode.valueOf(startupSettings.getLoginMode()))) {
                    registerAdminUser(startupSettings);
                }
            } else {
                registerAdminUser(startupSettings);
            }

            startupManager.startup();
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
        if (userService.hasActiveAdminUser()) {
            return;
        }

        String login = form.getAdminLogin();
        String password = form.getAdminPassword();
        String email = form.getAdminEmail();
        Locale locale = new Locale(form.getLanguage());

        List<String> roles = Arrays.asList(USER_ADMIN_ROLE, BUNDLE_ADMIN_ROLE, EMAIL_ADMIN_ROLE, SECURITY_ADMIN_ROLE, ROLES_ADMIN, MDS_ADMIN);

        userService.register(login, password, email, null, roles, locale);
    }

    public void setStartupFormValidatorFactory(StartupFormValidatorFactory validatorFactory) {
        this.startupFormValidatorFactory = validatorFactory;
    }
}
