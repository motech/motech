package org.motechproject.server.i18n;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;

import static java.lang.String.format;
import static java.util.Collections.enumeration;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class I18nRepositoryTest {

    private static final String I18N_RESOURCES_PATH = "webapp/messages/";

    @InjectMocks
    private I18nRepository i18nRepository = new I18nRepository();

    @Mock
    private BundleContext bundleContext;

    @Mock
    private Bundle bundleEnglish;

    @Mock
    private Bundle bundlePolish;

    @Mock
    private Bundle bundleTraditionalChinese;

    @Mock
    private Bundle bundleFrench;

    @Mock
    private Bundle bundleWithout;

    @Before
    public void setUp() {
        final Bundle[] bundleArray = new Bundle[]{ bundleEnglish, bundleFrench, bundlePolish, bundleTraditionalChinese, bundleWithout };
        // bundle tracker manipulates the returned arrays, so the mock has to return a new
        // copy each time it is called
        when(bundleContext.getBundles()).thenAnswer(new Answer<Bundle[]>() {
            @Override
            public Bundle[] answer(InvocationOnMock invocation) throws Throwable {
                return Arrays.copyOf(bundleArray, bundleArray.length);
            }
        });

        final String enPath = format("%smessages.properties", I18N_RESOURCES_PATH);
        final String plPath = format("%smessages_pl.properties", I18N_RESOURCES_PATH);
        final String frPath = format("%smessages_fr.properties", I18N_RESOURCES_PATH);
        final String zhPath = format("%smessages_zh_TW.Big5.properties", I18N_RESOURCES_PATH);

        when(bundleEnglish.getEntryPaths(I18N_RESOURCES_PATH)).thenReturn(enumeration(Collections.singletonList(enPath)));
        when(bundlePolish.getEntryPaths(I18N_RESOURCES_PATH)).thenReturn(enumeration(Collections.singletonList(plPath)));
        when(bundleFrench.getEntryPaths(I18N_RESOURCES_PATH)).thenReturn(enumeration(Collections.singletonList(frPath)));
        when(bundleTraditionalChinese.getEntryPaths(I18N_RESOURCES_PATH)).thenReturn(enumeration(Collections.singletonList(zhPath)));

        setUpMessagesFile(bundleEnglish, enPath, "en");
        setUpMessagesFile(bundlePolish, plPath, "pl");
        setUpMessagesFile(bundleFrench, frPath, "fr");
        setUpMessagesFile(bundleTraditionalChinese, zhPath, "zh_TW.Big5");

        for (Bundle bundle : bundleArray) {
            when(bundle.getState()).thenReturn(Bundle.ACTIVE);
        }
    }

    @Test
    public void shouldReturnAvailableLanguages() {
        i18nRepository.init();
        NavigableMap<String, String> map = i18nRepository.getLanguages();

        verify(bundleEnglish).getEntryPaths(I18N_RESOURCES_PATH);
        verify(bundlePolish).getEntryPaths(I18N_RESOURCES_PATH);
        verify(bundleFrench).getEntryPaths(I18N_RESOURCES_PATH);
        verify(bundleTraditionalChinese).getEntryPaths(I18N_RESOURCES_PATH);
        verify(bundleWithout).getEntryPaths(I18N_RESOURCES_PATH);

        assertEquals(4, map.size());

        assertTrue(map.containsKey("en"));
        assertEquals("English", map.get("en"));

        assertTrue(map.containsKey("pl"));
        assertEquals("Polski", map.get("pl"));

        assertTrue(map.containsKey("fr"));
        assertEquals("Français", map.get("fr"));

        assertTrue(map.containsKey("zh_TW.Big5"));
        assertEquals("中文", map.get("zh_TW.Big5"));
    }

    @Test
    public void shouldReturnMessages() {
        i18nRepository.init();

        Map<String, String> result = i18nRepository.getMessages(Locale.GERMAN);
        assertEquals(buildMsgMap(null, null, null), result); // all default

        result = i18nRepository.getMessages(Locale.ENGLISH);
        assertEquals(buildMsgMap(null, null, null), result); // english is default

        result = i18nRepository.getMessages(new Locale("pl"));
        assertEquals(buildMsgMap("pl1", "pl2", null), result);

        result = i18nRepository.getMessages(Locale.FRENCH);
        assertEquals(buildMsgMap("value fr", "something fr", "something else fr"), result);

        result = i18nRepository.getMessages(new Locale("zh", "TW", "Big5"));
        assertEquals(buildMsgMap("test zh", null, null), result);
    }

    private void setUpMessagesFile(Bundle bundle, String path, String lang) {
        String langSuffix = "en".equals(lang) ? "" : '_' + lang;
        when(bundle.getResource(path)).thenReturn(getClass().getClassLoader()
                .getResource("messages/messages" + langSuffix + ".properties"));
    }

    private Map<String, String> buildMsgMap(String val1, String val2, String val3) {
        Map<String, String> msgs = new HashMap<>();
        msgs.put("key1", val1 == null ? "value" : val1);
        msgs.put("key2", val2 == null ? "something" : val2);
        msgs.put("key3", val3 == null ? "something else" : val3);
        return msgs;
    }
}
