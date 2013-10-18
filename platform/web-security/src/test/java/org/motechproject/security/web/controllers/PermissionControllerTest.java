package org.motechproject.security.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.service.MotechPermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class PermissionControllerTest {

    @InjectMocks
    private PermissionController permissionController = new PermissionController();

    @Mock
    private MotechPermissionService permissionService;

    private MockMvc controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = MockMvcBuilders.standaloneSetup(permissionController).build();
    }

    @Test
    public void shouldSavePermissions() throws Exception {
        controller.perform(
                post("/permissions/testName")
        ).andExpect(
                status().is(HttpStatus.OK.value())
        );

        ArgumentCaptor<PermissionDto> captor = ArgumentCaptor.forClass(PermissionDto.class);
        verify(permissionService).addPermission(captor.capture());

        assertEquals("testName", captor.getValue().getPermissionName());
        assertNull(captor.getValue().getBundleName());
    }

    @Test
    public void shouldRemovePermissions() throws Exception {
        controller.perform(
                delete("/permissions/testName")
        ).andExpect(
                status().is(HttpStatus.OK.value())
        );

        verify(permissionService).deletePermission("testName");
    }
}
