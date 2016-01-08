package org.motechproject.security.service;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.model.RoleDto;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SecurityRoleLoaderTest {

    private SecurityRoleLoader securityRoleLoader;

    private static final String SYMBOLIC_NAME = "bundleSymName";

    @Mock
    private MotechRoleService roleService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private Bundle bundle;

    @Mock
    private MotechPermissionService permissionService;

    @Mock
    private Resource resource;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        securityRoleLoader = new SecurityRoleLoader(roleService, permissionService);

        when(bundleContext.getBundle()).thenReturn(bundle);
        when(bundle.getSymbolicName()).thenReturn(SYMBOLIC_NAME);
    }

    @Test
    public void shouldUpdateExistingRoles() throws IOException {
        when(roleService.getRole("Test Role")).thenReturn(new RoleDto("Test Role", Collections.<String>emptyList(), false));
        when(applicationContext.getResource("roles.json")).thenReturn(resource);
        when(applicationContext.getBean(BundleContext.class)).thenReturn(bundleContext);
        when(resource.exists()).thenReturn(true);
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("roles.json")) {
            when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(IOUtils.toByteArray(in)));
        }

        securityRoleLoader.loadRoles(applicationContext);

        verify(roleService).getRole("Test Role");

        ArgumentCaptor<RoleDto> roleCaptor = ArgumentCaptor.forClass(RoleDto.class);
        verify(roleService).updateRole(roleCaptor.capture());

        assertEquals("Test Role", roleCaptor.getValue().getRoleName());
        assertEquals(asList("perm1", "perm2"), roleCaptor.getValue().getPermissionNames());

        ArgumentCaptor<PermissionDto> permissionCaptor = ArgumentCaptor.forClass(PermissionDto.class);
        verify(permissionService, times(2)).addPermission(permissionCaptor.capture());
        verifyPermission("perm1", SYMBOLIC_NAME, permissionCaptor.getAllValues().get(0));
        verifyPermission("perm2", SYMBOLIC_NAME, permissionCaptor.getAllValues().get(1));
    }

    @Test
    public void shouldCreateNewRoles() throws IOException {
        when(roleService.getRole("Test Role")).thenReturn(null);
        when(applicationContext.getResource("roles.json")).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("roles.json")) {
            when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(IOUtils.toByteArray(in)));
        }

        securityRoleLoader.loadRoles(applicationContext);

        verify(roleService).getRole("Test Role");

        ArgumentCaptor<RoleDto> captor = ArgumentCaptor.forClass(RoleDto.class);
        verify(roleService).createRole(captor.capture());

        assertEquals("Test Role", captor.getValue().getRoleName());
        assertEquals(asList("perm1", "perm2"), captor.getValue().getPermissionNames());

        ArgumentCaptor<PermissionDto> permissionCaptor = ArgumentCaptor.forClass(PermissionDto.class);
        verify(permissionService, times(2)).addPermission(permissionCaptor.capture());
        verifyPermission("perm1", null, permissionCaptor.getAllValues().get(0));
        verifyPermission("perm2", null, permissionCaptor.getAllValues().get(1));
    }

    @Test
    public void shouldDoNothingForNotExistingResources() {
        when(applicationContext.getResource("roles.json")).thenReturn(resource);
        when(resource.exists()).thenReturn(false);

        securityRoleLoader.loadRoles(applicationContext);

        verify(roleService, never()).updateRole(any(RoleDto.class));
        verify(roleService, never()).createRole(any(RoleDto.class));

        verify(permissionService, never()).addPermission(any(PermissionDto.class));
    }

    private void verifyPermission(String expectedName, String expectedBundleName, PermissionDto permission) {
        assertNotNull(permission);
        assertEquals(expectedName, permission.getPermissionName());
        assertEquals(expectedBundleName, permission.getBundleName());
    }
}
