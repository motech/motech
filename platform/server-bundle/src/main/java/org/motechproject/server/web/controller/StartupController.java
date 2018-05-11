package org.motechproject.server.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.config.domain.LoginMode;
import org.motechproject.config.domain.MotechSettings;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.osgi.web.service.LocaleService;
import org.motechproject.server.web.dto.StartupViewData;
import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.motechproject.server.web.validator.StartupFormValidator;
import org.motechproject.server.web.validator.StartupFormValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.motechproject.server.web.controller.Constants.REDIRECT_HOME;

/**
 * StartupController that manages the platform system start up and captures the platform core settings and user information.
 */
@Controller
@Api(value = "StartupController", description = "StartupController that manages the platform system start up and captures " +
        "the platform core settings and user information.")
public class StartupController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartupController.class);

    @Autowired
    private StartupManager startupManager;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private LocaleService localeService;

    @Autowired
    private MotechUserService userService;

    @Autowired
    private StartupFormValidatorFactory startupFormValidatorFactory;

    @RequestMapping(value = "/startupviewdata", method = RequestMethod.GET)
    @ApiOperation(value = "Returns Motech startup ")
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
            viewData.setLanguages(localeService.getSupportedLanguages());
            viewData.setPageLang(userLocale);
            viewData.setIsFileMode(ConfigSource.FILE.equals(configSource));
            viewData.setIsAdminRegistered(userService.hasActiveMotechAdmin());
            viewData.setRedirectHome(false);
        }

        return viewData;
    }

    @RequestMapping(value = "/startup", method = RequestMethod.GET)
    public ModelAndView startup() {
        ModelAndView view = new ModelAndView("startup");

        if (startupManager.canLaunchBundles()) {
            view.setViewName(REDIRECT_HOME);
        }

        return view;
    }

    @RequestMapping(value = "/startup", method = RequestMethod.POST)
    @ApiOperation(value = "Configures Motech with the provided startup settings")
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
                settings.setProviderName(startupSettings.getProviderName());
                settings.setProviderUrl(startupSettings.getProviderUrl());

                configurationService.savePlatformSettings(settings);

                if (LoginMode.REPOSITORY.equals(LoginMode.valueOf(startupSettings.getLoginMode()))) {
                    registerMotechAdmin(startupSettings);
                }
            } else {
                registerMotechAdmin(startupSettings);
            }

            startupManager.startup();
        }

        return errors;
    }

    private StartupSuggestionsForm createSuggestions() {
        return new StartupSuggestionsForm();
    }

    private void registerMotechAdmin(StartupForm form) {
        if (userService.hasActiveMotechAdmin()) {
            LOGGER.warn("The admin user exists and is active");
            return;
        }

        String login = form.getAdminLogin();
        String password = form.getAdminPassword();
        String email = form.getAdminEmail();
        Locale locale = new Locale(form.getLanguage());

        LOGGER.info("Registering admin user");
        userService.registerMotechAdmin(login, password, email, locale);
    }

    public void setStartupFormValidatorFactory(StartupFormValidatorFactory validatorFactory) {
        this.startupFormValidatorFactory = validatorFactory;
    }
}
