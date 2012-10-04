package org.motechproject.server.ui;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.server.ui.impl.LocaleSettingsImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.NavigableMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocaleSettingsTest {
    private static final String I18N_RESOURCES_PATH = "webapp/resources/messages/";

    @Mock
    private CookieLocaleResolver cookieLocaleResolver;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private Bundle bundle;

    @InjectMocks
    private LocaleSettings localeSettings = new LocaleSettingsImpl();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetAvailableLanguages() {
        List<String> stringList = new ArrayList<>();
        stringList.add(String.format("%stest.properties", I18N_RESOURCES_PATH));
        stringList.add(String.format("%stest_en_US.properties", I18N_RESOURCES_PATH));
        stringList.add(String.format("%stest_pl.properties", I18N_RESOURCES_PATH));
        stringList.add(String.format("%stest_pl_PL.properties", I18N_RESOURCES_PATH));

        Enumeration<String> enumeration = Collections.enumeration(stringList);

        when(bundleContext.getBundle()).thenReturn(bundle);
        when(bundle.getEntryPaths(I18N_RESOURCES_PATH)).thenReturn(enumeration);

        NavigableMap<String, String> map = localeSettings.getAvailableLanguages();

        verify(bundleContext).getBundle();
        verify(bundle).getEntryPaths(I18N_RESOURCES_PATH);

        assertEquals(4, map.size());

        assertTrue(map.containsKey("en"));
        assertEquals("English", map.get("en"));

        assertTrue(map.containsKey("en_US"));
        assertEquals("English", map.get("en_US"));

        assertTrue(map.containsKey("pl"));
        assertEquals("Polski", map.get("pl"));

        assertTrue(map.containsKey("pl_PL"));
        assertEquals("Polski", map.get("pl_PL"));
    }
}
