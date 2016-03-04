package org.motechproject.security.model;

import java.util.List;

/**
 * Used to transfer security configuration
 * to and from a web request and UI
 */
public class SecurityConfigDto {
    private List<SecurityRuleDto> securityRules;

    public List<SecurityRuleDto> getSecurityRules() {
        return securityRules;
    }

    public void setSecurityRules(List<SecurityRuleDto> securityRules) {
        this.securityRules = securityRules;
    }
}
