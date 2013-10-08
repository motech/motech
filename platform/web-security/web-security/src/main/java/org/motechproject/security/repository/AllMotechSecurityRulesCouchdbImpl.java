package org.motechproject.security.repository;

import java.util.Collections;
import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Implementation of DAO interface that utilizes
 * a CouchDB back-end for storage. Only one
 * MotechSecurityConfiguration file should be saved
 * at a time, so adding the document looks for
 * the old document in order to update it if it
 * already exists. Rather than updating the object
 * reference, the old configuration's ID and revision
 * are used for the new document.
 */
@Component
@View(name = "all", map = "function(doc) { emit(doc._id, doc); }")
public class AllMotechSecurityRulesCouchdbImpl extends MotechBaseRepository<MotechSecurityConfiguration> implements AllMotechSecurityRules {

    @Autowired
    protected AllMotechSecurityRulesCouchdbImpl(@Qualifier("webSecurityDbConnector") CouchDbConnector db) {
        super(MotechSecurityConfiguration.class, db);
        initStandardDesignDocument();
    }

    @Override
    public void add(MotechSecurityConfiguration config) {
        List<MotechSecurityConfiguration> oldConfigList = this.getAll();
        if (oldConfigList.size() == 0) {
            super.add(config);
        } else {
            MotechSecurityConfiguration oldConfig = oldConfigList.get(0);
            config.setRevision(oldConfig.getRevision());
            config.setId(oldConfig.getId());
            super.update(config);
        }
    }

    @Override
    public List<MotechURLSecurityRule> getRules() {
        List<MotechSecurityConfiguration> config = this.getAll();

        return config.size() == 0 ? Collections.<MotechURLSecurityRule>emptyList() : config.get(0).getSecurityRules();
    }
}
