package org.motechproject.security.service;

import java.util.List;

import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Service to access and update security configuration details
 * from the platform. Permission based, method level security
 * is defined to prevent unauthorized users from updating security.
 */
public interface MotechURLSecurityService {

    /**
     * A protected method for viewing security rule
     * information for the platform.
     * @return All URL security rules found in the database
     */
    @PreAuthorize("hasAnyRole('viewSecurity', 'updateSecurity')")
    List<MotechURLSecurityRule> findAllSecurityRules();

    /**
     * A protected method for updating security configuration
     * for the platform.
     * @param configuration The updated security information, which will cause an updating of the motech proxy manager
     */
    @PreAuthorize("hasRole('updateSecurity')")
    void updateSecurityConfiguration(MotechSecurityConfiguration configuration);

}
