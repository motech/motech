package org.motechproject.security.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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
import com.google.gson.reflect.TypeToken;

/**
 * Helper class that scans an application context
 * for security rules and re-initializes the
 * MotechProxyManager security chain.
 */

@Component
public class SecurityRuleLoader {

    private static final String CONFIG_LOCATION = "securityRules.json";

    private MotechJsonReader motechJsonReader = new MotechJsonReader();

    @Autowired
    private AllMotechSecurityRules allSecurityRules;

    @Autowired
    private MotechProxyManager proxyManager;

    private static final Logger LOG = LoggerFactory.getLogger(SecurityRuleLoader.class);

    /**
     * Attempts to load rules from the application context,
     * if rules are found, the security configuration is
     * updated. Synchronized so there are not race conditions
     * on the data.
     */

    public synchronized void loadRules(ApplicationContext applicationContext) {
        LOG.debug("Loading rules");
        Resource securityResource = applicationContext.getResource(CONFIG_LOCATION);

        if (securityResource.exists()) {
            try (InputStream in = securityResource.getInputStream()) {
                List<MotechURLSecurityRule> rules = (List<MotechURLSecurityRule>)
                        motechJsonReader.readFromStream(in, new TypeToken<List<MotechURLSecurityRule>>() { } .getType());

                if (rules.size() > 0) {
                    updateSecurityConfig(rules);
                }

            } catch (IOException e) {
                LOG.error("Unable to security rules in " + applicationContext.getDisplayName(), e);
            }
        }
    }

    private void updateSecurityConfig(List<MotechURLSecurityRule> newRules) {

        //Assume all rules are of the same origin
        String origin = newRules.get(0).getOrigin();

        List<MotechURLSecurityRule> moduleRules = allSecurityRules.getRulesByOrigin(origin);

        if (moduleRules.size() > 0) {
            //Don't update security if rules from this origin have already been loaded
            return;
        }

        MotechSecurityConfiguration securityConfig = allSecurityRules.getMotechSecurityConfiguration();

        List<MotechURLSecurityRule> oldRules = securityConfig.getSecurityRules();

        newRules.addAll(oldRules);

        securityConfig.setSecurityRules(newRules);
        allSecurityRules.addOrUpdate(securityConfig);
        proxyManager.initializeProxyChain();
    }
}
