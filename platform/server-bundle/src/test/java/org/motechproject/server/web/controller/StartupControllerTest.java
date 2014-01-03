package org.motechproject.server.web.controller;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.domain.LoginMode;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleService;
import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.motechproject.server.web.helper.SuggestionHelper;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.TreeMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.security.UserRoleNames.BUNDLE_ADMIN_ROLE;
import static org.motechproject.security.UserRoleNames.EMAIL_ADMIN_ROLE;
import static org.motechproject.security.UserRoleNames.ROLES_ADMIN;
import static org.motechproject.security.UserRoleNames.SECURITY_ADMIN_ROLE;
import static org.motechproject.security.UserRoleNames.USER_ADMIN_ROLE;


public class StartupControllerTest {
    private static final String SUGGESTIONS_KEY = "suggestions";
    private static final String STARTUP_SETTINGS_KEY = "startupSettings";
    private static final String LANGUAGES_KEY = "languages";
    private static final String PAGE_LANG_KEY = "pageLang";
    private static final String IS_FILE_MODE_KEY = "isFileMode";
    private static final String HEADER_KEY = "mainHeader";
    private static final String REQUIRES_CONFIG_FILES = "requireConfigFiles";
    private static final String IS_ADMIN_REGISTERED = "isAdminRegistered";


    @Mock
    private StartupManager startupManager;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private LocaleService localeService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private SettingsRecord motechSettings;

    @Mock
    private MotechUserService userService;

    @Mock
    private SuggestionHelper suggestionHelper;

    @InjectMocks
    private StartupController startupController = new StartupController();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testStartup() {
        Properties properties = new Properties();
        properties.put("host", "localhost");
        properties.put("port", "12345");
        properties.put(ConfigurationConstants.AMQ_BROKER_URL, "test_url");

        NavigableMap<String, String> map = new TreeMap<>();

        when(startupManager.canLaunchBundles()).thenReturn(false);
        when(startupManager.getDefaultSettings()).thenReturn(motechSettings);

        when(motechSettings.getActivemqProperties()).thenReturn(properties);

        when(localeService.getUserLocale(httpServletRequest)).thenReturn(new Locale("en"));
        when(localeService.getAvailableLanguages()).thenReturn(map);

        when(configurationService.getPlatformSettings()).thenReturn(motechSettings);

        ModelAndView result = startupController.startup(httpServletRequest);

        verify(startupManager).canLaunchBundles();
        verify(localeService).getAvailableLanguages();
        verify(localeService).getUserLocale(httpServletRequest);

        assertEquals("startup", result.getViewName());
        assertModelMap(result.getModelMap(), SUGGESTIONS_KEY, STARTUP_SETTINGS_KEY, LANGUAGES_KEY, PAGE_LANG_KEY,
                IS_FILE_MODE_KEY, HEADER_KEY, REQUIRES_CONFIG_FILES, IS_ADMIN_REGISTERED);

        StartupSuggestionsForm startupSuggestionsForm = (StartupSuggestionsForm) result.getModelMap().get(SUGGESTIONS_KEY);

        assertTrue(startupSuggestionsForm.getDatabaseUrls().isEmpty());
        assertTrue(startupSuggestionsForm.getQueueUrls().isEmpty());
        assertTrue(startupSuggestionsForm.getSchedulerUrls().isEmpty());

        StartupForm startupSettings = (StartupForm) result.getModelMap().get(STARTUP_SETTINGS_KEY);
        Boolean requiresConfigFiles = (Boolean) result.getModelMap().get(REQUIRES_CONFIG_FILES);

        Assert.assertFalse(requiresConfigFiles);
        assertEquals("en", startupSettings.getLanguage());
    }

    @Test
    public void testStartupRedirectToHome() {
        when(startupManager.canLaunchBundles()).thenReturn(true);
        ModelAndView result = startupController.startup(httpServletRequest);

        assertEquals("redirect:home", result.getViewName());
    }


    @Test
    public void testSubmitFormStart() {
        StartupForm startupForm = startupForm();
        startupForm.setLoginMode(LoginMode.REPOSITORY.getName());
        when(bindingResult.hasErrors()).thenReturn(false);
        when(startupManager.getDefaultSettings()).thenReturn(motechSettings);
        when(configurationService.getPlatformSettings()).thenReturn(motechSettings);

        ModelAndView result = startupController.submitForm(startupForm, bindingResult);

        verify(configurationService).savePlatformSettings(any(MotechSettings.class));
        verify(startupManager).startup();
        verifyUserRegistration();

        assertEquals("redirect:home", result.getViewName());
    }

    @Test
    public void testSubmitFormOpenId() {
        StartupForm startupForm = startupForm();
        startupForm.setLoginMode(LoginMode.OPEN_ID.getName());
        when(bindingResult.hasErrors()).thenReturn(false);
        when(startupManager.getDefaultSettings()).thenReturn(motechSettings);
        when(configurationService.getPlatformSettings()).thenReturn(motechSettings);

        ModelAndView result = startupController.submitForm(startupForm, bindingResult);

        verify(configurationService).savePlatformSettings(any(MotechSettings.class));
        verify(startupManager).startup();
        verify(userService, never()).register(anyString(), anyString(), anyString(), anyString(), anyListOf(String.class), any(Locale.class));

        assertEquals("redirect:home", result.getViewName());
    }

    @Test
    public void shouldAddErrorsAndOtherFlagsInModelWhenValidationFails() {
        when(bindingResult.hasErrors()).thenReturn(true);
        ObjectError error = new ObjectError("loginMode", new String[]{"error.required.loginMode"}, null, "LogIn Mode Required");
        List<ObjectError> objectErrors = Arrays.asList(error);
        when(bindingResult.getAllErrors()).thenReturn(objectErrors);

        when(userService.hasActiveAdminUser()).thenReturn(true);

        ModelAndView modelAndView = startupController.submitForm(startupForm(), bindingResult);
        assertThat(modelAndView.getViewName(), Is.is("startup"));
        List<String> errors = (List<String>) modelAndView.getModelMap().get("errors");
        assertThat(errors.contains("error.required.loginMode"), Is.is(true));
        assertThat((Boolean) modelAndView.getModelMap().get("isAdminRegistered"), Is.is(true));
    }


    @Test
    public void shouldInformViewThatConfigFilesRequiredWhenConfigSourceIsFileAndConfigFilesDoNotExist() throws IOException {
        when(configurationService.requiresConfigurationFiles()).thenReturn(true);
        when(localeService.getUserLocale(httpServletRequest)).thenReturn(new Locale("en"));

        BootstrapConfig bootstrapConfig = mock(BootstrapConfig.class);
        when(bootstrapConfig.getConfigSource()).thenReturn(ConfigSource.FILE);
        when(configurationService.loadBootstrapConfig()).thenReturn(bootstrapConfig);

        when(configurationService.requiresConfigurationFiles()).thenReturn(true);

        ModelAndView modelAndView = startupController.startup(httpServletRequest);
        assertThat((Boolean) modelAndView.getModelMap().get("requireConfigFiles"), Is.is(true));
        verify(configurationService).requiresConfigurationFiles();
    }

    @Test
    public void shouldInformViewThatConfigFilesNotRequiredWhenConfigSourceIsFileAndConfigFilesExist() throws IOException {
        when(configurationService.requiresConfigurationFiles()).thenReturn(true);
        when(localeService.getUserLocale(httpServletRequest)).thenReturn(new Locale("en"));

        BootstrapConfig bootstrapConfig = mock(BootstrapConfig.class);
        when(bootstrapConfig.getConfigSource()).thenReturn(ConfigSource.FILE);
        when(configurationService.loadBootstrapConfig()).thenReturn(bootstrapConfig);

        when(configurationService.requiresConfigurationFiles()).thenReturn(false);

        ModelAndView modelAndView = startupController.startup(httpServletRequest);
        assertThat((Boolean) modelAndView.getModelMap().get("requireConfigFiles"), Is.is(false));
        verify(configurationService).requiresConfigurationFiles();
    }

    @Test
    public void shouldInformViewThatConfigFilesNotRequiredWhenConfigSourceIsUI() throws IOException {
        when(configurationService.requiresConfigurationFiles()).thenReturn(true);
        when(localeService.getUserLocale(httpServletRequest)).thenReturn(new Locale("en"));

        BootstrapConfig bootstrapConfig = mock(BootstrapConfig.class);
        when(bootstrapConfig.getConfigSource()).thenReturn(ConfigSource.UI);
        when(configurationService.loadBootstrapConfig()).thenReturn(bootstrapConfig);

        ModelAndView modelAndView = startupController.startup(httpServletRequest);
        assertThat((Boolean) modelAndView.getModelMap().get("requireConfigFiles"), Is.is(false));
        verify(configurationService, never()).requiresConfigurationFiles();
    }


    @Test
    public void shouldAddFlagIndicatingAbsenceOfAdminUser() {
        when(localeService.getUserLocale(httpServletRequest)).thenReturn(new Locale("en", "US"));
        when(userService.hasActiveAdminUser()).thenReturn(false);

        ModelAndView startup = startupController.startup(httpServletRequest);
        Boolean isAdminRegistered = (Boolean) startup.getModelMap().get(IS_ADMIN_REGISTERED);

        assertThat(isAdminRegistered, Is.is(false));
        verify(userService).hasActiveAdminUser();
    }

    @Test
    public void shouldNotRegisterAdminUserIfActiveAdminUserAlreadyExists() {
        StartupForm startupForm = startupForm();
        startupForm.setLoginMode(LoginMode.REPOSITORY.getName());

        when(bindingResult.hasErrors()).thenReturn(false);
        when(startupManager.getDefaultSettings()).thenReturn(motechSettings);
        when(startupManager.canLaunchBundles()).thenReturn(true);
        when(configurationService.getPlatformSettings()).thenReturn(motechSettings);

        when(userService.hasActiveAdminUser()).thenReturn(true);

        startupController.submitForm(startupForm, bindingResult);

        verify(userService, never()).register(anyString(), anyString(), anyString(), anyString(), anyListOf(String.class), any(Locale.class));

    }

    @Test
    public void shouldNotAllowStartupPostAfterStartup() {
        StartupForm form = new StartupForm();
        form.setLoginMode(LoginMode.REPOSITORY.getName());

        when(startupManager.canLaunchBundles()).thenReturn(true);
        when(bindingResult.hasErrors()).thenReturn(false);

        ModelAndView mav = startupController.submitForm(form, bindingResult);

        assertEquals("redirect:home", mav.getViewName());
        verify(userService, never()).register(anyString(), anyString(), anyString(), anyString(), anyListOf(String.class), any(Locale.class));
        verify(startupManager, never()).startup();
    }

    private void assertModelMap(final ModelMap modelMap, String... keys) {
        assertEquals(keys.length, modelMap.size());

        for (String k : keys) {
            if (!k.equalsIgnoreCase(HEADER_KEY)) {
                assertNotNull(modelMap.get(k));
            }
        }
    }

    private StartupForm startupForm() {
        StartupForm startupForm = new StartupForm();

        startupForm.setLanguage("en");
        startupForm.setQueueUrl("test_queue_url");
        startupForm.setSchedulerUrl("test_scheduler_url");
        startupForm.setAdminLogin("motech");
        startupForm.setAdminEmail("motech@motech.com");
        startupForm.setAdminPassword("motech");
        startupForm.setAdminConfirmPassword("motech");
        startupForm.setProviderName("Provider");
        startupForm.setProviderUrl("test_provider_url");

        return startupForm;
    }

    private void verifyUserRegistration() {
        verify(userService).register(eq("motech"), eq("motech"), eq("motech@motech.com"), eq((String) null),
                argThat(new ArgumentMatcher<List<String>>() {
                    @Override
                    public boolean matches(Object argument) {
                        List<String> val = (List<String>) argument;
                        return val.equals(Arrays.asList(USER_ADMIN_ROLE, BUNDLE_ADMIN_ROLE, EMAIL_ADMIN_ROLE,
                                SECURITY_ADMIN_ROLE,
                                ROLES_ADMIN));
                    }
                }), eq(Locale.ENGLISH));
    }
}
