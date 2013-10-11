package org.motechproject.security.repository;

import java.util.List;

import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;

/**
 * Repository for crud operations related to Motech security config.
 */
public interface AllMotechSecurityRules {

    /**
     * Add the security configuration to be saved
     * @param config The security configuration to add
     */
    void add(MotechSecurityConfiguration config);

    /**
     * Update the existing security configuration
     * @param config The security configuration to update
     */
    void update(MotechSecurityConfiguration config);

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
}
