package org.motechproject.security.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * The MotechSecurityConfiguration is a single document that contains all of the URL security rule
 * configuration. The configuration was designed as one document because the entire filter chain
 * must be reconstructed each time it is updated, therefore managing many references is unnecessary.
 */
public class MotechSecurityConfiguration {
    private List<MotechURLSecurityRule> securityRules;

    public MotechSecurityConfiguration() {
        this(new ArrayList<MotechURLSecurityRule>());
    }

    public MotechSecurityConfiguration(List<MotechURLSecurityRule> securityRules) {
        this.securityRules = securityRules;
    }

    public List<MotechURLSecurityRule> getSecurityRules() {
        return securityRules;
    }

    public void setSecurityRules(List<MotechURLSecurityRule> securityRules) {
        this.securityRules = securityRules;
    }
}
