package org.motechproject.security.service;

import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.security.builder.SecurityRuleBuilder;
import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.repository.AllMotechSecurityRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * The MotechProxyManager acts as a wrapper around Spring's FilterChainProxy.
 * The FilterChainProxy contains a list of immutable SecurityFilterChain objects
 * which Spring's security consults for filters when handling requests. In order
 * to dynamically define new secure, a new FilterChainProxy is constructed and the
 * reference is updated. The MotechProxyManager acts as a customized delegate
 * in MotechDelegatingFilterProxy.
 */
@Component
public class MotechProxyManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MotechProxyManager.class);
    private static final String DEFAULT_SECURITY_CONFIG_FILE = "defaultSecurityConfig.json";
    private static final String SYSTEM_ORIGIN = "SYSTEM_PLATFORM";

    @Autowired
    private FilterChainProxy proxy;

    @Autowired
    private SecurityRuleBuilder securityRuleBuilder;

    @Autowired
    private MotechURLSecurityService motechSecurityService;

    @Autowired
    private AllMotechSecurityRules securityRulesDAO;

    private MotechJsonReader motechJsonReader = new MotechJsonReader();

    /**
     * Method to invoke to dynamically re-define the Spring security.
     * All rules converted into security filter chains in order
     * to create a new FilterChainProxy. The order of the rules in the
     * list matters for filtering purposes.
     */
    public synchronized void rebuildProxyChain() {
        LOGGER.info("Rebuilding proxy chain");
        updateSecurityChain(motechSecurityService.findAllSecurityRules());
        LOGGER.info("Rebuilt proxy chain");
    }

    /**
     * This method serves the same purpose of rebuildProxyChain, but does not require
     * any kind of security authentication so it should only ever be used by the activator,
     * which does not have an authentication object.
     */
    public void initializeProxyChain() {
        LOGGER.info("Initializing proxy chain");

        MotechSecurityConfiguration securityConfiguration = securityRulesDAO.getMotechSecurityConfiguration();
        if (securityConfiguration == null) {
            securityConfiguration = new MotechSecurityConfiguration();
        }

        List<MotechURLSecurityRule> securityRules = securityConfiguration.getSecurityRules();
        List<MotechURLSecurityRule> systemRules = getDefaultSecurityConfiguration().getSecurityRules();

        for (MotechURLSecurityRule rule : systemRules) {
            if (!securityRules.contains(rule)) {
                LOGGER.debug("Found new rule, not present in database. Adding.");
                securityRules.add(rule);
            }
        }

        // remove rules that have origin set to SYSTEM_PLATFORM and are no longer in the default configuration
        Iterator<MotechURLSecurityRule> it = securityRules.iterator();
        while(it.hasNext()) {
            MotechURLSecurityRule ruleFromDb = it.next();
            if (SYSTEM_ORIGIN.equals(ruleFromDb.getOrigin()) && !systemRules.contains(ruleFromDb)) {
                it.remove();
            }
        }

        securityRulesDAO.addOrUpdate(securityConfiguration);

        updateSecurityChain(securityRules);
        LOGGER.info("Initialized proxy chain");
    }

    /**
     * This method reads default security configuration from the file containing security rules and
     * returns it.
     * @return MotechSecurityConfiguration default security rules
     */
    public MotechSecurityConfiguration getDefaultSecurityConfiguration() {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_SECURITY_CONFIG_FILE)) {
            LOGGER.debug("Load default security rules from: {}", DEFAULT_SECURITY_CONFIG_FILE);
            return (MotechSecurityConfiguration) motechJsonReader.readFromStream(in, MotechSecurityConfiguration.class);
        } catch (IOException e) {
            throw new MotechException("Error while loading json file", e);
        }
    }

    public FilterChainProxy getFilterChainProxy() {
        return proxy;
    }

    public void setFilterChainProxy(FilterChainProxy proxy) {
        this.proxy = proxy;
    }

    private void updateSecurityChain(List<MotechURLSecurityRule> securityRules) {
        LOGGER.debug("Updating security chain");

        // sort rules by priority descending
        TreeSet<MotechURLSecurityRule> sortedRules = new TreeSet<>(new Comparator<MotechURLSecurityRule>() {
            @Override
            public int compare(MotechURLSecurityRule o1, MotechURLSecurityRule o2) {
                Integer priority1 = o1.getPriority();
                Integer priority2 = o2.getPriority();

                int result = priority2.compareTo(priority1);
                return (result == 0) ? 1 : result; // do not return 0(equals)
            }
        });
        sortedRules.addAll(securityRules);

        List<SecurityFilterChain> newFilterChains = new ArrayList<>();

        for (MotechURLSecurityRule securityRule : sortedRules) {
            if (securityRule.isActive() && !securityRule.isDeleted()) {
                LOGGER.debug("Creating SecurityFilterChain for: {}", securityRule.getPattern());
                for (String method : securityRule.getMethodsRequired()) {
                    newFilterChains.add(securityRuleBuilder.buildSecurityChain(securityRule, method));
                }
                LOGGER.debug("Created SecurityFilterChain for: {}", securityRule.getPattern());
            }
        }

        LOGGER.debug("Updated security chain.");

        proxy = new FilterChainProxy(newFilterChains);
    }

}
