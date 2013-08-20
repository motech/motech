package org.motechproject.server.ui;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.ui.impl.LocaleSettingsImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Locale;
import java.util.NavigableMap;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.enumeration;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocaleSettingsTest {
    private static final String I18N_RESOURCES_PATH = "webapp/messages/";

    @Mock
    private MotechUserService userService;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private Bundle bundleEnglish;

    @Mock
    private Bundle bundlePolish;

    @Mock
    private Bundle bundleFrench;

    @Mock
    private Bundle bundleWithout;

    @Mock
    private Principal principal;

    @Mock
    private HttpServletRequest request;

    @Mock
    private CookieLocaleResolver cookieLocaleResolver;

    @InjectMocks
    private LocaleSettings localeSettings = new LocaleSettingsImpl();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetAvailableLanguages() {
        when(bundleContext.getBundles()).thenReturn(new Bundle[]{
                bundleEnglish, bundleFrench, bundlePolish, bundleWithout
        });

        when(bundleEnglish.getEntryPaths(I18N_RESOURCES_PATH)).thenReturn(enumeration(asList(format("%smessages.properties", I18N_RESOURCES_PATH))));
        when(bundlePolish.getEntryPaths(I18N_RESOURCES_PATH)).thenReturn(enumeration(asList(format("%smessages_pl.properties", I18N_RESOURCES_PATH))));
        when(bundleFrench.getEntryPaths(I18N_RESOURCES_PATH)).thenReturn(enumeration(asList(format("%smessages_fr.properties", I18N_RESOURCES_PATH))));

        NavigableMap<String, String> map = localeSettings.getAvailableLanguages();

        verify(bundleContext).getBundles();
        verify(bundleEnglish).getEntryPaths(I18N_RESOURCES_PATH);
        verify(bundlePolish).getEntryPaths(I18N_RESOURCES_PATH);
        verify(bundleFrench).getEntryPaths(I18N_RESOURCES_PATH);
        verify(bundleWithout).getEntryPaths(I18N_RESOURCES_PATH);

        assertEquals(3, map.size());

        assertTrue(map.containsKey("en"));
        assertEquals("English", map.get("en"));

        assertTrue(map.containsKey("pl"));
        assertEquals("Polski", map.get("pl"));

        assertTrue(map.containsKey("fr"));
        assertEquals("Français", map.get("fr"));
    }

    @Test
    public void shouldRetrieveLocales() {
        when(cookieLocaleResolver.resolveLocale(request)).thenReturn(Locale.CHINA);

        assertEquals(Locale.CHINA, localeSettings.getUserLocale(request));

        when(userService.getLocale("user")).thenReturn(Locale.GERMANY);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("user");

        assertEquals(Locale.GERMANY, localeSettings.getUserLocale(request));

        when(userService.getLocale("user")).thenReturn(null);
        when(cookieLocaleResolver.resolveLocale(request)).thenReturn(Locale.CANADA);

        assertEquals(Locale.CANADA, localeSettings.getUserLocale(request));
    }
}
