package org.motechproject.security.authentication;

import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.util.Assert;

public class MotechBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {


    public static final String SECURITY_REALM_KEY = "security.realm";

    @Autowired
    public MotechBasicAuthenticationEntryPoint(SettingsFacade settingsFacade) {
        String realmName = settingsFacade.getProperty(SECURITY_REALM_KEY);
        Assert.hasText(realmName, "realmName must be specified");
        setRealmName(realmName);
    }


}
