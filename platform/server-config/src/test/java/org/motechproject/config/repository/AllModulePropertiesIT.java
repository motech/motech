package org.motechproject.config.repository;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.config.domain.ModulePropertiesRecord;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class AllModulePropertiesIT extends SpringIntegrationTest {

    @Autowired
    private CouchDbConnector propertiesDbConnector;

    @Autowired
    private AllModuleProperties allModuleProperties;

    @Before
    @After
    public void setUp() throws Exception {
        markForDeletion(allModuleProperties.getAll());
        this.deleteAll();
    }

    @Test
    public void shouldBulkAddOrUpdate() throws Exception {
        propertiesDbConnector.create(createModulePropertiesRecord("existingFile", "existingKey", "existingValue"));
        List<ModulePropertiesRecord> recordsToAddOrUpdate = new ArrayList<>();
        recordsToAddOrUpdate.add(createModulePropertiesRecord("existingFile", "updatedKey", "updatedValue"));
        recordsToAddOrUpdate.add(createModulePropertiesRecord("newFile", "newKey", "newValue"));

        allModuleProperties.bulkAddOrUpdate(recordsToAddOrUpdate);

        List<ModulePropertiesRecord> dbRecords = allModuleProperties.getAll();
        assertEquals(2, dbRecords.size());
        assertTrue(dbRecords.containsAll(recordsToAddOrUpdate));
    }

    private ModulePropertiesRecord createModulePropertiesRecord(String fileName, String key, String value) {
        Properties properties = new Properties();
        properties.setProperty(key, value);
        return new ModulePropertiesRecord(properties, "module", fileName, false);
    }

    @Override
    @Autowired
    public CouchDbConnector getDBConnector() {
        return propertiesDbConnector;
    }
}
