package org.motechproject.commons.sql.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.sql.service.SqlDBManager;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class SqlDBManagerBundleIT extends BasePaxIT {

    @Override
    protected boolean shouldFakeModuleStartupEvent() {
        return true;
    }

    @Inject
    public SqlDBManager sqlDBManager;

    @Test
    public void shouldCreateDatabase() {
        // create database
        sqlDBManager.createDatabase("${sql.url}motech_test_db");
        // check if database exists
        assertTrue(sqlDBManager.checkForDatabase("${sql.url}motech_test_db"));
        // try to create it again - if it was created properly before then method should return false
        assertFalse(sqlDBManager.createDatabase("${sql.url}motech_test_db"));
    }
}
