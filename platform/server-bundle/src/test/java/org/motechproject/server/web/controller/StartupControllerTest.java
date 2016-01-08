package org.motechproject.server.web.controller;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.osgi.web.LocaleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.domain.LoginMode;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.web.dto.StartupViewData;
import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.motechproject.server.web.helper.SuggestionHelper;
import org.motechproject.server.web.validator.StartupFormValidatorFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.TreeMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class StartupControllerTest {

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
        startupController.setStartupFormValidatorFactory(new StartupFormValidatorFactory());
    }

    @Test
    public void testStartup() {
        Properties properties = new Properties();
        properties.put("host", "localhost");
        properties.put("port", "12345");

        NavigableMap<String, String> map = new TreeMap<>();

        when(startupManager.canLaunchBundles()).thenReturn(false);
        when(startupManager.getDefaultSettings()).thenReturn(motechSettings);

        when(localeService.getUserLocale(httpServletRequest)).thenReturn(new Locale("en"));
        when(localeService.getSupportedLanguages()).thenReturn(map);

        when(configurationService.getPlatformSettings()).thenReturn(motechSettings);

        StartupViewData result = startupController.getStartupViewData(httpServletRequest);

        verify(startupManager).canLaunchBundles();
        verify(localeService).getSupportedLanguages();
        verify(localeService).getUserLocale(httpServletRequest);

        assertThat(result.getRedirectHome(), Is.is(false));

        StartupSuggestionsForm startupSuggestionsForm = result.getSuggestions();

        assertTrue(startupSuggestionsForm.getDatabaseUrls().isEmpty());
        assertTrue(startupSuggestionsForm.getQueueUrls().isEmpty());
        assertTrue(startupSuggestionsForm.getSchedulerUrls().isEmpty());

        StartupForm startupSettings = result.getStartupSettings();
        Boolean requiresConfigFiles = result.getRequireConfigFiles();

        Assert.assertFalse(requiresConfigFiles);
        assertEquals("en", startupSettings.getLanguage());
    }

    @Test
    public void testStartupRedirectToHome() {
        when(startupManager.canLaunchBundles()).thenReturn(true);
        ModelAndView result = startupController.startup();

        assertEquals("redirect:home", result.getViewName());
    }

    @Test
    public void testSubmitFormStart() throws IOException {
        StartupForm startupForm = startupForm();
        startupForm.setLoginMode(LoginMode.REPOSITORY.getName());
        when(startupManager.getDefaultSettings()).thenReturn(motechSettings);
        when(configurationService.getPlatformSettings()).thenReturn(motechSettings);

        List<String> result = startupController.submitForm(startupForm);

        assertTrue(result.isEmpty());
        verify(configurationService).savePlatformSettings(any(MotechSettings.class));
        verify(startupManager).startup();
        verifyUserRegistration();
    }

    @Test
    public void testSubmitFormOpenId() throws IOException {
        StartupForm startupForm = startupForm();
        startupForm.setProviderUrl("https://www.example.com/accounts/id");
        startupForm.setLoginMode(LoginMode.OPEN_ID.getName());
        when(startupManager.getDefaultSettings()).thenReturn(motechSettings);
        when(configurationService.getPlatformSettings()).thenReturn(motechSettings);

        List<String> result = startupController.submitForm(startupForm);

        assertTrue(result.isEmpty());
        verify(configurationService).savePlatformSettings(any(MotechSettings.class));
        verify(startupManager).startup();
        verify(userService, never()).registerMotechAdmin(anyString(), anyString(), anyString(), any(Locale.class));
    }

    @Test
    public void shouldAddErrorsWhenValidationFails() throws IOException {
        when(userService.hasActiveMotechAdmin()).thenReturn(true);
        when(configurationService.getConfigSource()).thenReturn(ConfigSource.UI);
        StartupForm startupForm = startupForm();
        startupForm.setLanguage("");

        List<String> errors = startupController.submitForm(startupForm);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void shouldInformViewThatConfigFilesRequiredWhenConfigSourceIsFileAndConfigFilesDoNotExist() throws IOException {
        when(configurationService.requiresConfigurationFiles()).thenReturn(true);
        when(localeService.getUserLocale(httpServletRequest)).thenReturn(new Locale("en"));

        BootstrapConfig bootstrapConfig = mock(BootstrapConfig.class);
        when(bootstrapConfig.getConfigSource()).thenReturn(ConfigSource.FILE);
        when(configurationService.loadBootstrapConfig()).thenReturn(bootstrapConfig);

        when(configurationService.requiresConfigurationFiles()).thenReturn(true);

        StartupViewData startupViewData = startupController.getStartupViewData(httpServletRequest);
        assertThat((startupViewData.getRequireConfigFiles()), Is.is(true));
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

        StartupViewData startupViewData = startupController.getStartupViewData(httpServletRequest);
        assertThat((startupViewData.getRequireConfigFiles()), Is.is(false));
        verify(configurationService).requiresConfigurationFiles();
    }

    @Test
    public void shouldInformViewThatConfigFilesNotRequiredWhenConfigSourceIsUI() throws IOException {
        when(configurationService.requiresConfigurationFiles()).thenReturn(true);
        when(localeService.getUserLocale(httpServletRequest)).thenReturn(new Locale("en"));

        BootstrapConfig bootstrapConfig = mock(BootstrapConfig.class);
        when(bootstrapConfig.getConfigSource()).thenReturn(ConfigSource.UI);
        when(configurationService.loadBootstrapConfig()).thenReturn(bootstrapConfig);

        StartupViewData startupViewData = startupController.getStartupViewData(httpServletRequest);
        assertThat(startupViewData.getRequireConfigFiles(), Is.is(false));
        verify(configurationService, never()).requiresConfigurationFiles();
    }

    @Test
    public void shouldAddFlagIndicatingAbsenceOfAdminUser() {
        when(localeService.getUserLocale(httpServletRequest)).thenReturn(new Locale("en", "US"));
        when(userService.hasActiveMotechAdmin()).thenReturn(false);

        Boolean isAdminRegistered = startupController.getStartupViewData(httpServletRequest).getIsAdminRegistered();

        assertThat(isAdminRegistered, Is.is(false));
        verify(userService).hasActiveMotechAdmin();
    }

    @Test
    public void shouldNotRegisterAdminUserIfActiveAdminUserAlreadyExists() throws IOException {
        StartupForm startupForm = startupForm();
        startupForm.setLoginMode(LoginMode.REPOSITORY.getName());

        when(startupManager.getDefaultSettings()).thenReturn(motechSettings);
        when(startupManager.canLaunchBundles()).thenReturn(true);
        when(configurationService.getPlatformSettings()).thenReturn(motechSettings);
        when(userService.hasActiveMotechAdmin()).thenReturn(true);

        startupController.submitForm(startupForm);

        verify(userService, never()).registerMotechAdmin(anyString(), anyString(), anyString(), any(Locale.class));
    }

    @Test
    public void shouldNotAllowStartupPostAfterStartup() throws IOException {
        StartupForm form = startupForm();
        form.setLoginMode(LoginMode.REPOSITORY.getName());

        when(startupManager.getDefaultSettings()).thenReturn(motechSettings);
        when(startupManager.canLaunchBundles()).thenReturn(true);

        List<String> result = startupController.submitForm(form);

        assertTrue(result.isEmpty());
        verify(userService, never()).registerMotechAdmin(anyString(), anyString(), anyString(), any(Locale.class));
        verify(startupManager, never()).startup();
    }

    private StartupForm startupForm() {
        StartupForm startupForm = new StartupForm();

        startupForm.setLanguage("en");
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
        verify(userService).registerMotechAdmin(eq("motech"), eq("motech"), eq("motech@motech.com"), eq(Locale.ENGLISH));
    }
}
