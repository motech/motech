package org.motechproject.security.service.impl;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.AuthoritiesService;
import org.motechproject.security.service.MotechRoleService;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthoritiesServiceImplTest {

    @Mock
    private MotechRoleService motechRoleService;

    @InjectMocks
    private AuthoritiesService authoritiesService = new AuthoritiesServiceImpl();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldRetrieveAuthorities() {
        MotechUser user = mock(MotechUser.class);
        RoleDto role = mock(RoleDto.class);
        List<String> roles = Arrays.asList("role1");
        when(user.getRoles()).thenReturn(roles);

        when(motechRoleService.getRole("role1")).thenReturn(role);

        List<String> permissions = Arrays.asList("permission1");
        when(role.getPermissionNames()).thenReturn(permissions);

        List<GrantedAuthority> authorities = authoritiesService.authoritiesFor(user);

        assertThat(authorities.size(), Is.is(1));
        assertThat(authorities.get(0).getAuthority(), Is.is("permission1"));

    }

}
