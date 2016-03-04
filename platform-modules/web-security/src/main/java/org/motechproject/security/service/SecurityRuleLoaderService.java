package org.motechproject.security.service;

import org.springframework.context.ApplicationContext;

/**
 * Service that scans an application context
 * for security rules and re-initializes the
 * MotechProxyManager security chain.
 */
public interface SecurityRuleLoaderService {

    /**
     * Attempts to load rules from the application context,
     * if rules are found, the security configuration is
     * updated.
     */
    void loadRules(ApplicationContext applicationContext);
}
