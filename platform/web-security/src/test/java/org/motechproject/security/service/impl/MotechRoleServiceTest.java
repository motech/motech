package org.motechproject.security.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.repository.MotechUsersDao;
import org.motechproject.security.mds.MotechRolesDataService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.UserContextService;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MotechRoleServiceTest {


    @InjectMocks
    private MotechRoleService motechRoleService;

    @Mock
    private MotechRolesDataService rolesDataService;

    @Mock
    private MotechUsersDao motechUsersDao;

    @Mock
    private UserContextService userContextsService;

    @Before
    public void before() {
        motechRoleService = new MotechRoleServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldRefreshUserContextWhenRoleIsCreated() {
        RoleDto role = new RoleDto("role1", asList("permission1"));

        motechRoleService.createRole(role);

        verify(userContextsService).refreshAllUsersContextIfActive();
    }

    @Test
    public void shouldRefreshUserContextWhenRoleIsUpdated() {
        RoleDto role = new RoleDto("role1", asList("permission1"));

        MotechRole motechRole = mock(MotechRole.class);
        when(rolesDataService.findByRoleName("role1")).thenReturn(motechRole);

        motechRoleService.updateRole(role);

        verify(userContextsService).refreshAllUsersContextIfActive();
    }

    @Test
    public void shouldRefreshUserContextWhenRoleIsDeleted() {
        RoleDto role = new RoleDto("role1", asList("permission1"));

        MotechRole motechRole = mock(MotechRole.class);
        when(motechRole.isDeletable()).thenReturn(true);
        when(rolesDataService.findByRoleName("role1")).thenReturn(motechRole);

        motechRoleService.deleteRole(role);

        verify(userContextsService).refreshAllUsersContextIfActive();
    }


}
