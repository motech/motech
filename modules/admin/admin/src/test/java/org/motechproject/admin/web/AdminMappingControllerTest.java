package org.motechproject.admin.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.service.AdminMappingService;
import org.motechproject.admin.web.controller.AdminMappingController;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AdminMappingControllerTest {

    @InjectMocks
    AdminMappingController controller = new AdminMappingController();

    @Mock
    AdminMappingService adminMappingService;

    @Mock
    Map<String, String> mappings;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetMappings() {
        when(adminMappingService.getAllMappings()).thenReturn(mappings);

        Map<String, String> result = adminMappingService.getAllMappings();

        assertEquals(mappings, result);
        verify(adminMappingService).getAllMappings();
    }
}
