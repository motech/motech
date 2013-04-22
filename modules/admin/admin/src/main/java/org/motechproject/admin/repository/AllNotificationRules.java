package org.motechproject.admin.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.admin.domain.NotificationRule;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllNotificationRules extends MotechBaseRepository<NotificationRule> {

    @Autowired
    protected AllNotificationRules(@Qualifier("adminDbConnector") CouchDbConnector db) {
        super(NotificationRule.class, db);
    }
}
