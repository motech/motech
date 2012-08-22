package org.motechproject.dao;

import org.ektorp.CouchDbConnector;
import org.motechproject.model.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllRules extends MotechBaseRepository<Rule> {
    @Autowired
    public AllRules(@Qualifier("ruleDatabase") CouchDbConnector db) {
        super(Rule.class, db);
    }
}
