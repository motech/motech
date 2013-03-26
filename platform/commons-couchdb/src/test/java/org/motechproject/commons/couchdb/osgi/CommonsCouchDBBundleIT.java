package org.motechproject.commons.couchdb.osgi;

import org.ektorp.CouchDbConnector;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.spring.HttpClientFactoryBean;
import org.ektorp.support.GenerateView;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.dao.BusinessIdNotUniqueException;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.commons.couchdb.service.CouchDbManager;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class CommonsCouchDBBundleIT extends BaseOsgiIT {

    public void testCouchDbManager() throws Exception {
        ServiceReference registryReference = bundleContext.getServiceReference(CouchDbManager.class.getName());
        assertNotNull(registryReference);
        CouchDbManager service = (CouchDbManager) bundleContext.getService(registryReference);
        assertNotNull(service);
        CouchDbConnector dbConnector = service.getConnector("foo");
        assertEquals(System.getProperty("user.name") + "_"+"foo", dbConnector.getDatabaseName());
    }

    public void testCommonsCouchDB() throws Exception {
        CouchDbConnector connector = getConnector();
        TestRepository repository = new TestRepository(TestRecord.class, connector);
        repository.addOrReplace(new TestRecord("test"));
        repository.add(new TestRecord("test"));
        try {
            repository.addOrReplace(new TestRecord("test"));
            fail("Expected BusinessIdNotUniqueException");
        } catch (BusinessIdNotUniqueException e) {
        }
        finally {
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

    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.commons.couchdb.service");
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] {"/testApplicationCommonsCouchdbBundleContext.xml"};
    }

    class TestRepository extends MotechBaseRepository<TestRecord> {
        protected TestRepository(Class<TestRecord> type, CouchDbConnector db) {
            super(type, db);
        }

        protected void addOrReplace(TestRecord entity) {
            super.addOrReplace(entity, "name", entity.getName());
        }
        @GenerateView
        List<TestRecord> findByName(String name)  {
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
