package org.motechproject.openmrs.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.security.InvalidCredentialsException;
import org.motechproject.mrs.security.MRSUser;
import org.openmrs.api.context.ContextAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenMRSAuthenticationProviderTest {
    @Mock
    private UsernamePasswordAuthenticationToken authentication;
    @Mock
    private MRSUser user;
    @Mock
    private OpenMRSSession openMRSSession;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldAuthenticateForAValidUsernameAndPasswordCombination() {
        when(authentication.getCredentials()).thenReturn("password");
        when(openMRSSession.authenticate("username","password")).thenReturn(user);
        MRSUser authenticatedUser = new OpenMRSAuthenticationProvider(openMRSSession).retrieveUser("username", authentication);
        assertThat(authenticatedUser, is(equalTo(user)));
    }

    @Test(expected = InvalidCredentialsException.class)
    public void shouldRaiseAuthenticationExceptionForContextAuthenticationException() {
        when(authentication.getCredentials()).thenReturn("password");
        when(openMRSSession.authenticate("username","password")).thenThrow(new ContextAuthenticationException());
        new OpenMRSAuthenticationProvider(openMRSSession).retrieveUser("username", authentication);
    }

}
