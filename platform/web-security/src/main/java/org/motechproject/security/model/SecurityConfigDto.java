package org.motechproject.security.model;

import org.motechproject.security.domain.MotechURLSecurityRule;

import java.util.List;

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
