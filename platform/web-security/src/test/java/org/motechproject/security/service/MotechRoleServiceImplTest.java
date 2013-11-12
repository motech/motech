package org.motechproject.security.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.repository.AllMotechUsers;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MotechRoleServiceImplTest {


    @InjectMocks
    private MotechRoleService motechRoleService;

    @Mock
    private AllMotechRoles allMotechRoles;

    @Mock
    private AllMotechUsers allMotechUsers;

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
        when(allMotechRoles.findByRoleName("role1")).thenReturn(motechRole);

        motechRoleService.updateRole(role);

        verify(userContextsService).refreshAllUsersContextIfActive();
    }

    @Test
    public void shouldRefreshUserContextWhenRoleIsDeleted() {
        RoleDto role = new RoleDto("role1", asList("permission1"));

        MotechRole motechRole = mock(MotechRole.class);
        when(motechRole.isDeletable()).thenReturn(true);
        when(allMotechRoles.findByRoleName("role1")).thenReturn(motechRole);

        motechRoleService.deleteRole(role);

        verify(userContextsService).refreshAllUsersContextIfActive();
    }


}
