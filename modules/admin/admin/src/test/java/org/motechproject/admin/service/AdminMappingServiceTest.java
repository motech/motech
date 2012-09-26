package org.motechproject.admin.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.domain.AdminMapping;
import org.motechproject.admin.repository.AllAdminMappings;
import org.motechproject.admin.service.impl.AdminMappingServiceImpl;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.MotechSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AdminMappingServiceTest {

    private static final String GRAPHITE_URL = "http://graphite.motechproject.org";

    @InjectMocks
    AdminMappingService adminMappingService = new AdminMappingServiceImpl();

    @Mock
    AllAdminMappings allAdminMappings;

    @Mock
    MotechSettings motechSettings;

    @Mock
    PlatformSettingsService platformSettingsService;

    List<AdminMapping> mappings = new ArrayList<>();

    AdminMapping firstMapping = new AdminMapping("bundle1", "b1/test");
    AdminMapping secondMapping = new AdminMapping("bundle2", "url");


    @Before
    public void setUp() {
        initMocks(this);
        mappings.add(firstMapping);
        mappings.add(secondMapping);
    }

    @Test
    public void testGetAllMappings() {
        when(allAdminMappings.getAll()).thenReturn(mappings);

        Map<String, String> result = adminMappingService.getAllMappings();

        assertEquals(mappings.size(), result.size());
        assertEquals(result.get(firstMapping.getBundleName()), firstMapping.getDestination());
        assertEquals(result.get(secondMapping.getBundleName()), secondMapping.getDestination());
        verify(allAdminMappings).getAll();
    }

    @Test
    public void testGetGraphiteUrl() {
        when(platformSettingsService.getPlatformSettings()).thenReturn(motechSettings);
        when(motechSettings.getMetricsProperties()).thenReturn(metricsProperties());

        String result = adminMappingService.getGraphiteUrl();

        assertEquals(GRAPHITE_URL, result);
        verify(platformSettingsService).getPlatformSettings();
        verify(motechSettings).getMetricsProperties();
    }

    @Test
    public void testUnRegisterMapping() {
        adminMappingService.unregisterMapping(firstMapping.getBundleName());
        verify(allAdminMappings).removeByBundleName(firstMapping.getBundleName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullBundleNameRegistration() {
        adminMappingService.registerMapping(null, "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullUrlNameRegistration() {
        adminMappingService.registerMapping("test", null);
    }

    private Properties metricsProperties() {
        Properties props = new Properties();
        props.put(MotechSettings.GRAPHITE_URL, GRAPHITE_URL);
        return  props;
    }
}
