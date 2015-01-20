package org.motechproject.commons.sql.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.sql.service.SqlDBManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:testCommonsSqlContext.xml"})
public class SqlDBManagerIT {

    private static final Logger LOG = LoggerFactory.getLogger(SqlDBManagerIT.class);
    private static final int DB_EXISTS_CODE = 1007;
    private static final String DB_DUPLICATE_SQL_STATE = "42P04";

    @Autowired
    @Qualifier("sqlDbManager")
    public SqlDBManager sqlDBManager;

    @Autowired
    @Qualifier("sqlProperties")
    public Properties sqlProperties;

    @Test
    public void shouldCreateDatabase() {
        // create database
        sqlDBManager.createDatabase("motech_test_db");
        // check if database exists
        assertTrue(sqlDBManager.checkForDatabase("motech_test_db"));
        // try to create it again - if it was created properly before then method should return false
        assertFalse(sqlDBManager.createDatabase("motech_test_db"));
    }
}
