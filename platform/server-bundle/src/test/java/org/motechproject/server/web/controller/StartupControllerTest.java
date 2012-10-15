package org.motechproject.server.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.motechproject.server.config.settings.MotechSettings;
import org.motechproject.server.osgi.OsgiFrameworkService;
import org.motechproject.server.osgi.OsgiListener;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleSettings;
import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.TreeMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StartupManager.class, OsgiListener.class})
public class StartupControllerTest {
    private static final String SUGGESTIONS_KEY = "suggestions";
    private static final String STARTUP_SETTINGS_KEY = "startupSettings";
    private static final String LANGUAGES_KEY = "languages";

    @Mock
    private StartupManager startupManager;

    @Mock
    private PlatformSettingsService platformSettingsService;

    @Mock
    private LocaleSettings localeSettings;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private StartupController startupController = new StartupController();

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private ConfigFileSettings motechSettings;

    @Mock
    private OsgiFrameworkService osgiFrameworkService;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(StartupManager.class);
        PowerMockito.mockStatic(OsgiListener.class);

        initMocks(this);

        when(StartupManager.getInstance()).thenReturn(startupManager);
        when(OsgiListener.getOsgiService()).thenReturn(osgiFrameworkService);
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

        when(motechSettings.getCouchDBProperties()).thenReturn(properties);
        when(motechSettings.getActivemqProperties()).thenReturn(properties);
        when(motechSettings.getSchedulerProperties()).thenReturn(properties);

        when(localeSettings.getUserLocale(httpServletRequest)).thenReturn(new Locale("en"));
        when(localeSettings.getAvailableLanguages()).thenReturn(map);

        ModelAndView result = startupController.startup(httpServletRequest);

        verify(startupManager).canLaunchBundles();
        verify(localeSettings).getAvailableLanguages();
        verify(localeSettings).getUserLocale(httpServletRequest);

        assertEquals("startup", result.getViewName());
        assertEquals(3, result.getModelMap().size());
        assertNotNull(result.getModelMap().get(SUGGESTIONS_KEY));
        assertNotNull(result.getModelMap().get(STARTUP_SETTINGS_KEY));
        assertNotNull(result.getModelMap().get(LANGUAGES_KEY));

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
    public void testSubmitForm() {
        StartupForm startupForm = new StartupForm();
        startupForm.setLanguage("en");
        startupForm.setDatabaseUrl("test_db_url");
        startupForm.setQueueUrl("test_queue_url");
        startupForm.setSchedulerUrl("test_scheduler_url");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(startupManager.getLoadedConfig()).thenReturn(motechSettings);
        when(startupManager.canLaunchBundles()).thenReturn(true);

        ModelAndView result = startupController.submitForm(null, startupForm, bindingResult);

        verify(platformSettingsService).savePlatformSettings(any(Properties.class));
        verify(startupManager).startup();
        verify(startupManager).canLaunchBundles();
        verify(osgiFrameworkService).startBundle(anyString());

        assertEquals("redirect:home", result.getViewName());
    }
}
