package org.motechproject.server.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.security.helper.AuthenticationMode;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.motechproject.server.config.settings.MotechSettings;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleSettings;
import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.TreeMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StartupManager.class})
public class StartupControllerTest {
    private static final String SUGGESTIONS_KEY = "suggestions";
    private static final String STARTUP_SETTINGS_KEY = "startupSettings";
    private static final String LANGUAGES_KEY = "languages";
    private static final String PAGE_LANG_KEY = "pageLang";

    @Mock
    private StartupManager startupManager;

    @Mock
    private PlatformSettingsService platformSettingsService;

    @Mock
    private LocaleSettings localeSettings;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private ConfigFileSettings motechSettings;

    @Mock
    private MotechUserService userService;

    @InjectMocks
    private StartupController startupController = new StartupController();

    @Before
    public void setUp() {
        PowerMockito.mockStatic(StartupManager.class);

        initMocks(this);

        when(StartupManager.getInstance()).thenReturn(startupManager);
    }

    @Test
    public void testStartup() {
        Properties properties = new Properties();
        properties.put("host", "localhost");
        properties.put("port", "12345");
        properties.put(MotechSettings.AMQ_BROKER_URL, "test_url");
        properties.put(MotechSettings.SCHEDULER_URL, "test_url");

        NavigableMap<String, String> map = new TreeMap<>();

        when(startupManager.canLaunchBundles()).thenReturn(false);
        when(startupManager.findCouchDBInstance(anyString())).thenReturn(false);
        when(startupManager.findActiveMQInstance(anyString())).thenReturn(false);
        when(startupManager.findSchedulerInstance(anyString())).thenReturn(false);
        when(startupManager.getLoadedConfig()).thenReturn(motechSettings);

        when(motechSettings.getActivemqProperties()).thenReturn(properties);
        when(motechSettings.getSchedulerProperties()).thenReturn(properties);

        when(localeSettings.getUserLocale(httpServletRequest)).thenReturn(new Locale("en"));
        when(localeSettings.getAvailableLanguages()).thenReturn(map);

        ModelAndView result = startupController.startup(httpServletRequest);

        verify(startupManager).canLaunchBundles();
        verify(localeSettings).getAvailableLanguages();
        verify(localeSettings).getUserLocale(httpServletRequest);

        assertEquals("startup", result.getViewName());
        assertModelMap(result.getModelMap(), SUGGESTIONS_KEY, STARTUP_SETTINGS_KEY, LANGUAGES_KEY, PAGE_LANG_KEY);

        StartupSuggestionsForm startupSuggestionsForm = (StartupSuggestionsForm) result.getModelMap().get(SUGGESTIONS_KEY);

        assertTrue(startupSuggestionsForm.getDatabaseUrls().isEmpty());
        assertTrue(startupSuggestionsForm.getQueueUrls().isEmpty());
        assertTrue(startupSuggestionsForm.getSchedulerUrls().isEmpty());

        StartupForm startupSettings = (StartupForm) result.getModelMap().get(STARTUP_SETTINGS_KEY);

        assertEquals("en", startupSettings.getLanguage());
    }

    @Test
    public void testStartupRedirectToHome() {
        when(startupManager.canLaunchBundles()).thenReturn(true);
        ModelAndView result = startupController.startup(httpServletRequest);

        assertEquals("redirect:home", result.getViewName());
    }

    @Test
    public void testSubmitFormNoStart() {
        StartupForm startupForm = startupForm();
        startupForm.setLoginMode(AuthenticationMode.REPOSITORY);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(startupManager.getLoadedConfig()).thenReturn(motechSettings);
        when(startupManager.canLaunchBundles()).thenReturn(true);

        ModelAndView result = startupController.submitForm(null, startupForm, bindingResult);

        verify(platformSettingsService).savePlatformSettings(any(Properties.class));
        verify(startupManager).startup(false);
        verifyUserRegistration();

        assertEquals("redirect:home", result.getViewName());
    }

    @Test
    public void testSubmitFormStart() {
        StartupForm startupForm = startupForm();
        startupForm.setLoginMode(AuthenticationMode.REPOSITORY);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(startupManager.getLoadedConfig()).thenReturn(motechSettings);
        when(startupManager.canLaunchBundles()).thenReturn(true);

        ModelAndView result = startupController.submitForm("start", startupForm, bindingResult);

        verify(platformSettingsService).savePlatformSettings(any(Properties.class));
        verify(startupManager).startup(true);
        verifyUserRegistration();

        assertEquals("redirect:home", result.getViewName());
    }

    @Test
    public void testSubmitFormOpenId() {
        StartupForm startupForm = startupForm();
        startupForm.setLoginMode(AuthenticationMode.OPEN_ID);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(startupManager.getLoadedConfig()).thenReturn(motechSettings);
        when(startupManager.canLaunchBundles()).thenReturn(true);

        ModelAndView result = startupController.submitForm("start", startupForm, bindingResult);

        verify(platformSettingsService).savePlatformSettings(any(Properties.class));
        verify(startupManager).startup(true);
        verify(userService, never()).register(anyString(), anyString(), anyString(), anyString(), anyListOf(String.class));

        assertEquals("redirect:home", result.getViewName());
    }

    private void assertModelMap(final ModelMap modelMap, String... keys) {
        assertEquals(keys.length, modelMap.size());

        for (String k : keys) {
            assertNotNull(modelMap.get(k));
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
                return val.equals(Arrays.asList("Admin User", "Admin Bundle"));
            }
        }));
    }
}
