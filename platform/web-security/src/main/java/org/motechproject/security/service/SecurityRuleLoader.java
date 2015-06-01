package org.motechproject.security.service;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that scans an application context
 * for security rules and re-initializes the
 * MotechProxyManager security chain.
 */

@Component
public class SecurityRuleLoader {
    private static final String CONFIG_LOCATION = "securityRules.json";
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityRuleLoader.class);

    private MotechJsonReader motechJsonReader = new MotechJsonReader();

    private AllMotechSecurityRules allSecurityRules;
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
                        motechJsonReader.readFromStream(in, new TypeToken<List<MotechURLSecurityRule>>() {
                        }.getType());

                if (rules.size() > 0) {
                    updateSecurityConfig(rules);
                }

            } catch (IOException e) {
                LOGGER.error("Unable to load security rules from " + applicationContext.getDisplayName(), e);
            }
        }

        LOGGER.debug("Rules loaded from {}", applicationContext.getDisplayName());
    }

    /**
     * Updates existing Security config with new rules
     * if there're already rules with the same origin
     * as the first one since it means that it was
     * already loaded. Also update won't happen if
     * {@link org.motechproject.security.domain.MotechSecurityConfiguration}
     * cannot be set
     *
     * @param newRules list that contains new rules
     */
    private void updateSecurityConfig(List<MotechURLSecurityRule> newRules) {
        LOGGER.debug("Updating security config");

        String origin = newRules.get(0).getOrigin();
        String version = newRules.get(0).getVersion();

        LOGGER.debug("Rules origin: {}, version: {}", origin, version);

        List<MotechURLSecurityRule> moduleRules = allSecurityRules.getRulesByOriginAndVersion(origin, version);

        if (moduleRules.size() > 0) {
            //Don't update security if rules from this origin and the same version have already been loaded
            LOGGER.debug("Rules from the origin {} [version: {}] have already been loaded", origin, version);
            return;
        }

        LOGGER.debug("Updating config with rules from origin: {}", origin);

        MotechSecurityConfiguration securityConfig = allSecurityRules.getMotechSecurityConfiguration();

        if (securityConfig == null) {
            LOGGER.error("No security config found in the database");
            securityConfig = new MotechSecurityConfiguration();
        }

        List<MotechURLSecurityRule> oldRules = securityConfig.getSecurityRules();

        LOGGER.debug("Found " + oldRules.size() + " old rules in the database");

        newRules.addAll(rulesWithDifferentOrigin(oldRules, origin));

        LOGGER.debug("Saving rules from origin {} in the database", origin);

        securityConfig.setSecurityRules(newRules);
        allSecurityRules.addOrUpdate(securityConfig);

        LOGGER.debug("Initializing chain after security config update");
        proxyManager.initializeProxyChain();
    }

    private List<MotechURLSecurityRule> rulesWithDifferentOrigin(List<MotechURLSecurityRule> oldRules, String origin) {
        List<MotechURLSecurityRule> rules = new ArrayList<>();

        for (MotechURLSecurityRule oldRule : oldRules) {
            if (!StringUtils.equals(oldRule.getOrigin(), origin)) {
                rules.add(oldRule);
            }
        }

        return rules;
    }

    @Autowired
    public void setAllSecurityRules(AllMotechSecurityRules allSecurityRules) {
        this.allSecurityRules = allSecurityRules;
    }

    @Autowired
    public void setProxyManager(MotechProxyManager proxyManager) {
        this.proxyManager = proxyManager;
    }
}
