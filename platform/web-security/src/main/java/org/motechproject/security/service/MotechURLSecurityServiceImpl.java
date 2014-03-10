package org.motechproject.security.service;

import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.repository.AllMotechSecurityRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("motechURLSecurityService")
public class MotechURLSecurityServiceImpl implements MotechURLSecurityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MotechPermissionServiceImpl.class);

    @Autowired
    private AllMotechSecurityRules allSecurityRules;

    @Autowired
    private MotechProxyManager proxyManager;

    @Override
    public List<MotechURLSecurityRule> findAllSecurityRules() {
        return allSecurityRules.getRules();
    }

    @Override
    public void updateSecurityConfiguration(MotechSecurityConfiguration configuration) {
        LOGGER.info("Updating security configuration");

        List<MotechURLSecurityRule> newRules = new ArrayList<>(configuration.getSecurityRules());

        for (MotechURLSecurityRule rule : proxyManager.getDefaultSecurityConfiguration().getSecurityRules()) {
            if (!newRules.contains(rule)) {
                rule.setDeleted(true);
                newRules.add(rule);
            }
        }

        allSecurityRules.addOrUpdate(new MotechSecurityConfiguration(newRules));
        proxyManager.rebuildProxyChain();

        LOGGER.info("Updated security configuration");
    }
}
