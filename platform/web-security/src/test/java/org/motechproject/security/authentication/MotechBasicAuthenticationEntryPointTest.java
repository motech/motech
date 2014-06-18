package org.motechproject.security.authentication;

import org.junit.Test;
import org.motechproject.server.config.SettingsFacade;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class MotechBasicAuthenticationEntryPointTest {

    @Test
    public void shouldSetRealmValueAsGivenInSettings() {
        SettingsFacade settingsFacade = mock(SettingsFacade.class);
        given(settingsFacade.getProperty(MotechBasicAuthenticationEntryPoint.SECURITY_REALM_KEY)).willReturn("FOO");
        MotechBasicAuthenticationEntryPoint authenticationEntryPoint = new MotechBasicAuthenticationEntryPoint(settingsFacade);
        assertEquals("FOO", authenticationEntryPoint.getRealmName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfRealmNameNotSpecified() {
        SettingsFacade settingsFacade = mock(SettingsFacade.class);
        given(settingsFacade.getProperty(MotechBasicAuthenticationEntryPoint.SECURITY_REALM_KEY)).willReturn(null);
        new MotechBasicAuthenticationEntryPoint(settingsFacade);
    }

}
