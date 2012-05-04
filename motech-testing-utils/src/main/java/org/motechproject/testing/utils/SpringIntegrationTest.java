package org.motechproject.testing.utils;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class SpringIntegrationTest extends BaseUnitTest{

    private CouchDbConnector dbConnector;

    protected ArrayList<BulkDeleteDocument> toDelete;

    @Before
    public void before() {
        toDelete = new ArrayList<BulkDeleteDocument>();
        dbConnector = getDBConnector();
    }

    @After
    public void after() {
        deleteAll();
    }

    public abstract CouchDbConnector getDBConnector() ;

    protected void deleteAll() {
        if (toDelete.size() > 0)
            dbConnector.executeBulk(toDelete);
        toDelete.clear();
    }

    protected void markForDeletion(Object... documents) {
        for (Object document : documents)
            markForDeletion(document);
    }

    protected void markForDeletion(List documents) {
        markForDeletion(documents.toArray());
    }

    protected void markForDeletion(Object document) {
        toDelete.add(BulkDeleteDocument.of(document));
    }

    protected String unique(String name) {
        return name + DateUtil.now().toInstant().getMillis();
    }
}
