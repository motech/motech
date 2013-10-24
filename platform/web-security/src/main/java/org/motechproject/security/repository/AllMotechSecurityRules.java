package org.motechproject.security.repository;

import java.util.List;

import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;

/**
 * Repository for crud operations related to Motech security config.
 */
public interface AllMotechSecurityRules {

    /**
     * Remove the existing security configuration
     * @param config The security configuration to remove
     */
    void remove(MotechSecurityConfiguration config);

    /**
     * Convenience method for retrieving all of
     * the individual security rules from the security
     * configuration.
     * @return A list of all URL pattern security rules from the database
     */
    List<MotechURLSecurityRule> getRules();

    /**
     * Returns the full security configuration
     * @return The full MOTECH security configuration
     */
    MotechSecurityConfiguration getMotechSecurityConfiguration();

    /**
     * Add or update the MOTECH security configuration rules
     * @param securityConfig The security config for MOTECH
     */
    void addOrUpdate(MotechSecurityConfiguration securityConfig);

    List<MotechURLSecurityRule> getRulesByOrigin(String origin);
}
