package org.motechproject.security.model;

import java.util.List;

import org.motechproject.security.domain.MotechURLSecurityRule;

/**
 * Used to transfer security configuration
 * to and from a web request and UI
 */
public class SecurityConfigDto {

    private List<MotechURLSecurityRule> securityRules;

    public List<MotechURLSecurityRule> getSecurityRules() {
        return securityRules;
    }

    public void setSecurityRules(List<MotechURLSecurityRule> securityRules) {
        this.securityRules = securityRules;
    }
}
