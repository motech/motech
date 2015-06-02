package org.motechproject.security.repository;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.helper.IDTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of DAO interface that utilizes a MDS back-end for storage. Only one
 * MotechSecurityConfiguration file should be saved at a time, so adding the document looks for
 * the old document in order to update it if it already exists. Rather than updating the object
 * reference, the old configuration's ID and revision are used for the new document.
 */
@Repository
public class AllMotechSecurityRules {
    private static final Logger LOGGER = LoggerFactory.getLogger(AllMotechSecurityRules.class);

    private MotechURLSecurityRuleDataService dataService;

    /**
     * Reads rules from {@link org.motechproject.security.domain.MotechSecurityConfiguration}
     * and split them into those to be created, updated or removed.
     * Before updating {@link org.motechproject.security.repository.MotechURLSecurityRuleDataService}
     * is checked for old rule with the same id - update will be done
     * only if it exists. Same thing happens for rules to be removed.
     *
     *
     * @param config
     */
    public void addOrUpdate(MotechSecurityConfiguration config) {
        List<MotechURLSecurityRule> newRules = config.getSecurityRules();
        List<MotechURLSecurityRule> oldRules = dataService.retrieveAll();

        final Collection newRulesIDs = CollectionUtils.collect(newRules, IDTransformer.INSTANCE);
        final Collection oldRulesIDs = CollectionUtils.collect(oldRules, IDTransformer.INSTANCE);

        List<MotechURLSecurityRule> create = new ArrayList<>(newRules);
        CollectionUtils.filter(create, new MotechSecurityRulePredicate() {
            @Override
            protected boolean match(MotechURLSecurityRule rule) {
                return null == rule.getId();
            }
        });

        List<MotechURLSecurityRule> update = new ArrayList<>(newRules);
        CollectionUtils.filter(update, new MotechSecurityRulePredicate() {
            @Override
            protected boolean match(MotechURLSecurityRule rule) {
                return null != rule.getId() && oldRulesIDs.contains(rule.getId());
            }
        });

        List<MotechURLSecurityRule> delete = new ArrayList<>(oldRules);
        CollectionUtils.filter(delete, new MotechSecurityRulePredicate() {
            @Override
            protected boolean match(MotechURLSecurityRule rule) {
                return null != rule.getId() && !newRulesIDs.contains(rule.getId());
            }
        });

        LOGGER.debug("Processing rules: {}/{}/{} (Create/Update/Delete)", create.size(), update.size(), delete.size());

        for (MotechURLSecurityRule rule : create) {
            dataService.create(rule);
        }

        for (MotechURLSecurityRule rule : update) {
            dataService.update(rule);
        }

        for (MotechURLSecurityRule rule : delete) {
            dataService.delete(rule);
        }

        LOGGER.debug("Processed rules: {}/{}/{} (Create/Update/Delete)", create.size(), update.size(), delete.size());
    }

    /**
     * Returns all MotechURLSecurityRules
     *
     * @return list that contains rules
     */
    public List<MotechURLSecurityRule> getRules() {
        List<MotechURLSecurityRule> rules = dataService.retrieveAll();
        Iterator<MotechURLSecurityRule> iterator = rules.iterator();

        while (iterator.hasNext()) {
            MotechURLSecurityRule rule = iterator.next();

            if (rule.isDeleted()) {
                iterator.remove();
            }
        }

        return rules;
    }

    /**
     * Gets MotechSecurityConfiguration
     *
     * @return configuration
     */
    public MotechSecurityConfiguration getMotechSecurityConfiguration() {
        return new MotechSecurityConfiguration(dataService.retrieveAll());
    }

    /**
     * Returns all MotechURLSecurityRules for given origin
     *
     * @param origin of security rules
     * @return list that contains rules or null in case origin is null
     */
    public List<MotechURLSecurityRule> getRulesByOrigin(String origin) {
        return null == origin ? null : dataService.findByOrigin(origin);
    }

    /**
     * Returns all MotechURLSecurityRules for given origin and version
     *
     * @param origin of security rules
     * @param version of security rules
     * @return list that contains rules or null in case origin or version is null
     */
    public List<MotechURLSecurityRule> getRulesByOriginAndVersion(String origin, String version) {
        return null == origin || null == version ? null : dataService.findByOriginAndVersion(origin, version);
    }

    /**
     * Returns MotechURLSecurityRule for given id
     *
     * @param id of security rule
     * @return rule with given id or null in case when id == null
     */
    public MotechURLSecurityRule getRuleById(Long id) {
        return null == id ? null : dataService.retrieve("id", id);
    }

    /**
     * Removes all rules from given MotechSecurityConfiguration
     *
     * @param config with rules to be removed
     */
    public void remove(MotechSecurityConfiguration config) {
        for (MotechURLSecurityRule rule : config.getSecurityRules()) {
            dataService.delete(rule);
        }
    }

    @Autowired
    public void setDataService(MotechURLSecurityRuleDataService dataService) {
        this.dataService = dataService;
    }

    private abstract static class MotechSecurityRulePredicate implements Predicate {

        @Override
        public boolean evaluate(Object object) {
            boolean match = object instanceof MotechURLSecurityRule;

            if (match) {
                MotechURLSecurityRule rule = (MotechURLSecurityRule) object;
                match = match(rule);
            }

            return match;
        }

        protected abstract boolean match(MotechURLSecurityRule rule);

    }

}
