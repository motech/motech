package org.motechproject.security.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.MotechRoleService;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class RoleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MotechRoleService roleService;

    @InjectMocks
    private RoleController roleController = new RoleController();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();
    }

    @Test
    public void shouldGetRoleDetailsGivenItsName() throws Exception {
        String roleName = "foo";
        RoleDto role = new RoleDto(roleName, Arrays.asList("permission1"));
        when(roleService.getRole(roleName)).thenReturn(role);

        mockMvc.perform(
                get(String.format("/web-api/roles/role/%s", roleName)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"roleName\":\"foo\",\"originalRoleName\":\"foo\",\"permissionNames\":[\"permission1\"],\"deletable\":false}"));

    }

}
