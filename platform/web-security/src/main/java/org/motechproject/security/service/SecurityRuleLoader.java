package org.motechproject.security.service;

import com.google.gson.reflect.TypeToken;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.repository.AllMotechSecurityRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Helper class that scans an application context
 * for security rules and re-initializes the
 * MotechProxyManager security chain.
 */

@Component
public class SecurityRuleLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityRuleLoader.class);

    private static final String CONFIG_LOCATION = "securityRules.json";

    private MotechJsonReader motechJsonReader = new MotechJsonReader();

    @Autowired
    private AllMotechSecurityRules allSecurityRules;

    @Autowired
    private MotechProxyManager proxyManager;

    /**
     * Attempts to load rules from the application context,
     * if rules are found, the security configuration is
     * updated. Synchronized so there are not race conditions
     * on the data.
     */
    public synchronized void loadRules(ApplicationContext applicationContext) {
        LOGGER.debug("Loading rules from {}", applicationContext.getDisplayName());
        Resource securityResource = applicationContext.getResource(CONFIG_LOCATION);

        if (securityResource.exists()) {
            LOGGER.debug("File {} exists in {}", CONFIG_LOCATION, applicationContext.getDisplayName());

            try (InputStream in = securityResource.getInputStream()) {
                List<MotechURLSecurityRule> rules = (List<MotechURLSecurityRule>)
                        motechJsonReader.readFromStream(in, new TypeToken<List<MotechURLSecurityRule>>() { } .getType());

                if (rules.size() > 0) {
                    updateSecurityConfig(rules);
                }

            } catch (IOException e) {
                LOGGER.error("Unable to load security rules from " + applicationContext.getDisplayName(), e);
            }
        }

        LOGGER.debug("Rules loaded from {}", applicationContext.getDisplayName());
    }

    private void updateSecurityConfig(List<MotechURLSecurityRule> newRules) {
        LOGGER.debug("Updating security config");

        String origin = newRules.get(0).getOrigin();
        LOGGER.debug("Rules origin: {}", origin);

        List<MotechURLSecurityRule> moduleRules = allSecurityRules.getRulesByOrigin(origin);

        if (moduleRules.size() > 0) {
            //Don't update security if rules from this origin have already been loaded
            LOGGER.debug("Rules from the origin {} have already been loaded", origin);
            return;
        }

        MotechSecurityConfiguration securityConfig = allSecurityRules.getMotechSecurityConfiguration();

        List<MotechURLSecurityRule> oldRules = securityConfig.getSecurityRules();

        newRules.addAll(oldRules);

        securityConfig.setSecurityRules(newRules);
        allSecurityRules.addOrUpdate(securityConfig);

        LOGGER.debug("Initializing chain after security config update");
        proxyManager.initializeProxyChain();
    }
}
