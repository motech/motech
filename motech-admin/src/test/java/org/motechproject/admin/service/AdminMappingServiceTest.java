package org.motechproject.admin.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.domain.AdminMapping;
import org.motechproject.admin.repository.AllAdminMappings;
import org.motechproject.admin.service.impl.AdminMappingServiceImpl;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.osgi.framework.BundleContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AdminMappingServiceTest {

    @InjectMocks
    AdminMappingService adminMappingService = new AdminMappingServiceImpl();

    @Mock
    AllAdminMappings allAdminMappings;

    @Mock
    BundleContext bundleContext;

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
}
