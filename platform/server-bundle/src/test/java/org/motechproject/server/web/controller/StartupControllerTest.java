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
import org.motechproject.server.config.domain.ConfigFileSettings;
import org.motechproject.server.config.settings.MotechSettings;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleService;
import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.motechproject.server.web.validator.StartupFormValidator;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.TreeMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
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
    private Errors errors;
    private static  final List<String> uriAssertFalseList = Arrays.asList("failoverr:(tcp://127.0.0.1:61616,tcp://127.0.0.1:61616)?initialReconnectDelay=100","failover:(tcp://localhost:61616,tcp://remotehost:61616)?initialReconnectDelay=100",
                                                                        "failover:(tcp://256.0.0.1:61616,tcp://127.0.0.1:61616)?initialReconnectDelay=100","failover:(tcp://127.0..0.1:61616,tcp://127.0.0.1:61616)?initialReconnectDelay=100",
                                                                        "failover:((tcp:///127.0.0.1:61616,tcp://127.0.0.1:61616))?initialReconnectDelay=100","failover:(tcp://127.0.0.1:61616,tcp://127.0.0.1:612616)?initialReconnectDelay=100",
                                                                        "failover:(tcp://127.0.0.1:61616,tcp://1217.0.0.1:61616)?initialReconnectDelay=100","failover://(tcp://137.0.0.1:61616,tcp://137.0.0.1:61616)?timeout=3000",
                                                                        "fanout:(static:(tcp:///localhost:61629,tcp://localhost:61639,tcp://localhost:61649))", "fanout:(staatic:(tcp://localhost:61629,tcp://localhost:61639,tcp://localhost:61649))",
                                                                        "vm:(brooker:(tcp://localhost:6000)?persistent=false)?marshal=false", "wjfwwfeweffwwewf", "  ", ".....");

    private static  final List<String> uriAssertTrueList = Arrays.asList("failover:(tcp://127.0.0.1:61616,tcp://127.0.0.1:61616)?initialReconnectDelay=100", "failover:(tcp://117.0.0.1:61616,tcp://117.0.0.1:61616)?randomize=false",
                                                                        "fanout:(static:(tcp://127.0.0.1:61629,tcp://127.0.0.1:61639,tcp://127.0.0.1:61649))", "failover:(tcp://192.168.42.100:61616,tcp://192.168.42.101:61616)",
                                                                        "failover:(tcp://137.0.0.1:61616,udp://137.0.0.1:61616)?randomize=false&priorityBackup=true", "vm:(broker:(tcp://127.0.0.1:6000)?persistent=false)?marshal=false",
                                                                        "failover:(vm://137.0.0.1:61616,http://137.0.0.1:61616,https://137.0.0.1:61616)?randomize=false&priorityBackup=true&priorityURIs=tcp://137.0.0.1:61616,tcp://137.0.0.1:61616",
                                                                        "vm:(static:(tcp://137.0.0.1:6000)?persistent=false)?marshal=false", "ssl://137.0.0.1:61616?transport.enabledCipherSuites=SSL_RSA_WITH_RC4_128_SHA,SSL_DH_anon_WITH_3DES_EDE_CBC_SHA");

    @Mock
    private StartupManager startupManager;

    @Mock
    private PlatformSettingsService platformSettingsService;

    @Mock
    private LocaleService localeService;

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
        when(startupManager.findActiveMQInstance(anyString())).thenReturn(false);
        when(startupManager.findSchedulerInstance(anyString())).thenReturn(false);
        when(startupManager.getDefaultSettings()).thenReturn(motechSettings);

        when(motechSettings.getActivemqProperties()).thenReturn(properties);
        when(motechSettings.getSchedulerProperties()).thenReturn(properties);

        when(localeService.getUserLocale(httpServletRequest)).thenReturn(new Locale("en"));
        when(localeService.getAvailableLanguages()).thenReturn(map);

        ModelAndView result = startupController.startup(httpServletRequest);

        verify(startupManager).canLaunchBundles();
        verify(localeService).getAvailableLanguages();
        verify(localeService).getUserLocale(httpServletRequest);

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
    public void testSubmitFormStart() {
        StartupForm startupForm = startupForm();
        startupForm.setLoginMode(AuthenticationMode.REPOSITORY);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(startupManager.getDefaultSettings()).thenReturn(motechSettings);
        when(startupManager.canLaunchBundles()).thenReturn(true);

        ModelAndView result = startupController.submitForm(startupForm, bindingResult);

        verify(platformSettingsService).savePlatformSettings(any(Properties.class));
        verify(startupManager).startup();
        verifyUserRegistration();

        assertEquals("redirect:home", result.getViewName());
    }

    @Test
    public void testSubmitFormOpenId() {
        StartupForm startupForm = startupForm();
        startupForm.setLoginMode(AuthenticationMode.OPEN_ID);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(startupManager.getDefaultSettings()).thenReturn(motechSettings);
        when(startupManager.canLaunchBundles()).thenReturn(true);

        ModelAndView result = startupController.submitForm(startupForm, bindingResult);

        verify(platformSettingsService).savePlatformSettings(any(Properties.class));
        verify(startupManager).startup();
        verify(userService, never()).register(anyString(), anyString(), anyString(), anyString(), anyListOf(String.class), any(Locale.class));

        assertEquals("redirect:home", result.getViewName());
    }

    @Test
    public void testUriValidation() {
        StartupForm startupForm = startupForm();
        StartupFormValidator validator =  new StartupFormValidator(userService);

        for(String uri : uriAssertFalseList) {
            errors = new BeanPropertyBindingResult(startupForm,"validQueue");
            validator.validateQueueUrl(errors, uri, "queueUrl");
            assertTrue(errors.hasErrors());
        }

        for(String uri : uriAssertTrueList) {
            errors = new BeanPropertyBindingResult(startupForm,"validQueue");
            validator.validateQueueUrl(errors, uri, "queueUrl");
            assertFalse(errors.hasErrors());
        }
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
                        return val.equals(Arrays.asList(StartupController.USER_ADMIN_ROLE, StartupController.BUNDLE_ADMIN_ROLE, StartupController.EMAIL_ADMIN_ROLE));
                    }
                }), eq(Locale.ENGLISH));
    }
}
