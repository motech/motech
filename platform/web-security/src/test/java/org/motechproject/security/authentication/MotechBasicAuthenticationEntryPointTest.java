package org.motechproject.security.authentication;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.motechproject.server.config.SettingsFacade;

import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class MotechBasicAuthenticationEntryPointTest {

    @Test
    public void shouldSetRealmValueAsGivenInSettings() {
        SettingsFacade settingsFacade = mock(SettingsFacade.class);
        given(settingsFacade.getProperty(MotechBasicAuthenticationEntryPoint.SECURITY_REALM_KEY)).willReturn("FOO");
        MotechBasicAuthenticationEntryPoint authenticationEntryPoint = new MotechBasicAuthenticationEntryPoint(settingsFacade);
        assertThat(authenticationEntryPoint.getRealmName(), Is.is("FOO"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfRealmNameNotSpecified() {
        SettingsFacade settingsFacade = mock(SettingsFacade.class);
        given(settingsFacade.getProperty(MotechBasicAuthenticationEntryPoint.SECURITY_REALM_KEY)).willReturn(null);
        MotechBasicAuthenticationEntryPoint authenticationEntryPoint = new MotechBasicAuthenticationEntryPoint(settingsFacade);
    }

}
