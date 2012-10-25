package org.motechproject.event.aggregation.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.event.aggregation.model.AggregationRuleRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllAggregationRules extends MotechBaseRepository<AggregationRuleRecord> {

    @Autowired
    public AllAggregationRules(@Qualifier("eventAggregationDbConnector") CouchDbConnector db) {
        super(AggregationRuleRecord.class, db);
    }

    @GenerateView
    public AggregationRuleRecord findByName(String name) {
        return singleResult(queryView("by_name", name));
    }

    public void addOrReplace(AggregationRuleRecord rule) {
        super.addOrReplace(rule, "name", rule.getName());
    }

    public void remove(String ruleName) {
        remove(findByName(ruleName));
    }
}
