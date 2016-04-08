package org.motechproject.server;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.commons.PlatformCommons;
import org.springframework.core.io.ResourceLoader;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommonsDataProviderTest {

    private static final String MOCK_MOTECH_VERSION = "1.0";
    private static final DateTime MOCK_NOW = DateTime.now();
    private static final LocalDate MOCK_TODAY = LocalDate.now();

    private static Map<String, String> lookupFields;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private PlatformCommons platformCommons;

    private CommonsDataProvider provider;

    @Before
    public void setUp() {
        initMocks(this);
        lookupFields = new HashMap<>();
        provider = new CommonsDataProvider(resourceLoader);
        provider.setPlatformCommons(platformCommons);
    }

    @Test
    public void shouldReturnNullIfClassIsNotSupported() {
        String clazz = Integer.class.getSimpleName();
        Object object = provider.lookup(clazz, "Platform commons", lookupFields);
        assertNull(object);
    }

    @Test
    public void shouldReturnCommons() {

        when(platformCommons.getMotechVersion()).thenReturn(MOCK_MOTECH_VERSION);
        when(platformCommons.getNow()).thenReturn(MOCK_NOW);
        when(platformCommons.getToday()).thenReturn(MOCK_TODAY);

        PlatformCommons commons =
                (PlatformCommons) provider.lookup(PlatformCommons.class.getSimpleName(), "Platform commons", lookupFields);

        assertNotNull(commons);
        assertEquals(commons.getMotechVersion(), MOCK_MOTECH_VERSION);
        assertEquals(commons.getNow(), MOCK_NOW);
        assertEquals(commons.getToday(), MOCK_TODAY);
    }
}
