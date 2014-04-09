package org.motechproject.commons.couchdb.osgi;

import org.ektorp.CouchDbConnector;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.spring.HttpClientFactoryBean;
import org.ektorp.support.GenerateView;
import org.ektorp.support.TypeDiscriminator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.Tenant;
import org.motechproject.commons.couchdb.dao.BusinessIdNotUniqueException;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.commons.couchdb.service.CouchDbManager;
import org.motechproject.testing.osgi.BasePaxIT;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.inject.Inject;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class CommonsCouchDBBundleIT extends BasePaxIT {

    @Inject
    private CouchDbManager couchDbManager;

    @Test
    public void testCouchDbManager() throws Exception {
        CouchDbConnector dbConnector = couchDbManager.getConnector("foo");
        String dbName = String.format("%s%s", Tenant.current().getSuffixedId(), "foo");
        assertEquals(dbName, dbConnector.getDatabaseName());
    }

    @Test
    public void testCommonsCouchDB() throws Exception {
        CouchDbConnector connector = getConnector();
        TestRepository repository = new TestRepository(TestRecord.class, connector);
        repository.addOrReplace(new TestRecord("test"));
        repository.add(new TestRecord("test"));
        try {
            repository.addOrReplace(new TestRecord("test"));
            fail("Expected BusinessIdNotUniqueException");
        } catch (BusinessIdNotUniqueException e) {
        } finally {
            repository.removeAll();
        }
    }

    private CouchDbConnector getConnector() throws Exception {
        HttpClientFactoryBean httpClientFactoryBean = new HttpClientFactoryBean();
        Properties couchDbProperties = new Properties();
        couchDbProperties.put("host", "localhost");
        httpClientFactoryBean.setProperties(couchDbProperties);
        httpClientFactoryBean.setTestConnectionAtStartup(true);
        httpClientFactoryBean.setCaching(false);
        httpClientFactoryBean.afterPropertiesSet();
        return new StdCouchDbConnector("test", new StdCouchDbInstance(httpClientFactoryBean.getObject()));
    }

    class TestRepository extends MotechBaseRepository<TestRecord> {
        protected TestRepository(Class<TestRecord> type, CouchDbConnector db) {
            super(type, db);
        }

        protected void addOrReplace(TestRecord entity) {
            super.addOrReplace(entity, "name", entity.getName());
        }

        @GenerateView
        List<TestRecord> findByName(String name) {
            return queryView("by_name", name);
        }
    }

    @TypeDiscriminator("doc.type === 'TestRecord'")
    public static class TestRecord extends MotechBaseDataObject {
        private String name;

        public TestRecord() {
        }

        public TestRecord(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
