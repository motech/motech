package org.motechproject.server.pillreminder.dao;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AllPillRegimens extends MotechAuditableRepository<PillRegimen> {

    @Autowired
    public AllPillRegimens(@Qualifier("pillReminderDatabase") CouchDbConnector db) {
        super(PillRegimen.class, db);
        initStandardDesignDocument();
    }
}
