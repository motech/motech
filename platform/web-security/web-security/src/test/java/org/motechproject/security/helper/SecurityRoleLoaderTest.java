package org.motechproject.security.helper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.MotechRoleService;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SecurityRoleLoaderTest {

    private SecurityRoleLoader securityRoleLoader;

    @Mock
    private MotechRoleService roleService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private Resource resource;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        securityRoleLoader = new SecurityRoleLoader(roleService);
    }

    @Test
    public void shouldUpdateExistingRoles() throws IOException {
        when(roleService.getRole("Test Role")).thenReturn(new RoleDto("Test Role", Collections.<String>emptyList(), false));
        when(applicationContext.getResource("roles.json")).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(getClass().getClassLoader().getResourceAsStream("roles.json"));

        securityRoleLoader.loadRoles(applicationContext);

        verify(roleService).getRole("Test Role");

        ArgumentCaptor<RoleDto> captor = ArgumentCaptor.forClass(RoleDto.class);
        verify(roleService).updateRole(captor.capture());

        assertEquals("Test Role", captor.getValue().getRoleName());
        assertEquals(asList("perm1", "perm2"), captor.getValue().getPermissionNames());
    }

    @Test
    public void shouldCreateNewRoles() throws IOException {
        when(roleService.getRole("Test Role")).thenReturn(null);
        when(applicationContext.getResource("roles.json")).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(getClass().getClassLoader().getResourceAsStream("roles.json"));

        securityRoleLoader.loadRoles(applicationContext);

        verify(roleService).getRole("Test Role");

        ArgumentCaptor<RoleDto> captor = ArgumentCaptor.forClass(RoleDto.class);
        verify(roleService).createRole(captor.capture());

        assertEquals("Test Role", captor.getValue().getRoleName());
        assertEquals(asList("perm1", "perm2"), captor.getValue().getPermissionNames());
    }

    @Test
    public void shouldDoNothingForNotExistingResources() {
        when(applicationContext.getResource("roles.json")).thenReturn(resource);
        when(resource.exists()).thenReturn(false);

        securityRoleLoader.loadRoles(applicationContext);

        verify(roleService, never()).updateRole(any(RoleDto.class));
        verify(roleService, never()).createRole(any(RoleDto.class));
    }
}
