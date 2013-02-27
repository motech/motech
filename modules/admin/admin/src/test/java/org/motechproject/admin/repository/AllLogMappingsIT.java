package org.motechproject.admin.repository;

import org.apache.log4j.Level;
import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.admin.domain.LogMapping;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:testApplicationAdmin.xml"})
public class AllLogMappingsIT extends SpringIntegrationTest {
    private static final String ADMIN_LOG_NAME = "org.motechproject.admin";
    private static final String REPOSITORY_LOG_NAME = ADMIN_LOG_NAME + ".repository";

    @Autowired
    private AllLogMappings allLogMappings;

    @Autowired
    @Qualifier("adminDbConnector")
    private CouchDbConnector couchDbConnector;

    @Test
    public void testByLogName() throws Exception {
        LogMapping expected = new LogMapping(REPOSITORY_LOG_NAME, Level.DEBUG.toString());
        allLogMappings.addOrUpdate(expected);

        LogMapping actual = allLogMappings.byLogName(REPOSITORY_LOG_NAME);

        assertEquals(expected, actual);

        markForDeletion(actual);
    }

    @Test
    public void testAddOrUpdate() throws Exception {
        List<LogMapping> expected = new ArrayList<>();
        expected.add(new LogMapping(ADMIN_LOG_NAME, Level.ERROR.toString()));
        expected.add(new LogMapping(REPOSITORY_LOG_NAME, Level.TRACE.toString()));

        for (LogMapping logMapping : expected) {
            allLogMappings.addOrUpdate(logMapping);
        }

        List<LogMapping> actual = allLogMappings.getAll();

        assertEquals(expected, actual);

        expected.get(0).setLogLevel(Level.INFO.toString());

        allLogMappings.addOrUpdate(expected.get(0));

        actual = allLogMappings.getAll();

        assertEquals(expected, actual);

        markForDeletion(actual);
    }

    @Test
    public void testRemoveByLogName() throws Exception {
        final LogMapping mapping = new LogMapping(REPOSITORY_LOG_NAME, Level.INFO.toString());
        allLogMappings.addOrUpdate(mapping);

        assertNotNull(allLogMappings.getAll());

        allLogMappings.removeByLogName(REPOSITORY_LOG_NAME);

        assertTrue(allLogMappings.getAll().isEmpty());
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return couchDbConnector;
    }
}
