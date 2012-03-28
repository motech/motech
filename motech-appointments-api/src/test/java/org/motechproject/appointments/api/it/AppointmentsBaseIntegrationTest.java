package org.motechproject.appointments.api.it;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.motechproject.model.MotechBaseDataObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;

public abstract class AppointmentsBaseIntegrationTest {
    @Qualifier("appointmentsDatabase")
    @Autowired
    protected CouchDbConnector dbConnector;

    private ArrayList<BulkDeleteDocument> toDelete;

    @Before
    public void before() {
        toDelete = new ArrayList<BulkDeleteDocument>();
    }

    @After
    public void after() {
        deleteAll();
    }

    protected void markForDeletion(MotechBaseDataObject... documents) {
        for (MotechBaseDataObject document : documents)
            toDelete.add(BulkDeleteDocument.of(document));
    }

    private void deleteAll() {
        dbConnector.executeBulk(toDelete);
        toDelete.clear();
    }
}
