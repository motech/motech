package org.motechproject.admin.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AllStatusMessages extends MotechBaseRepository<StatusMessage> {

    @Autowired
    public AllStatusMessages(@Qualifier("adminDatabase")CouchDbConnector connector) {
        super(StatusMessage.class, connector);
    }
}
