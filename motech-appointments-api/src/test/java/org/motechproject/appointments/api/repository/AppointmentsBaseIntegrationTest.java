package org.motechproject.appointments.api.repository;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;

@ContextConfiguration(locations = {"/applicationAppointmentsAPI.xml"})
public class AppointmentsBaseIntegrationTest {

    @Qualifier("appointmentsDatabase")
    @Autowired
    protected CouchDbConnector dbConnector;

    protected ArrayList<BulkDeleteDocument> toDelete;

    @Before
    public void before() {
        toDelete = new ArrayList<BulkDeleteDocument>();
    }

    @After
    public void after() {
        deleteAll();
    }

    protected void deleteAll() {
        dbConnector.executeBulk(toDelete);
        toDelete.clear();
    }

    protected void markForDeletion(Object... documents) {
        for (Object document : documents)
            markForDeletion(document);
    }

    protected void markForDeletion(Object document) {
        toDelete.add(BulkDeleteDocument.of(document));
    }

    protected String unique(String name) {
        return name + DateUtil.now().toInstant().getMillis();
    }
}
