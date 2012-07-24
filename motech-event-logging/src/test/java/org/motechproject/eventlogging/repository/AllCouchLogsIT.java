package org.motechproject.eventlogging.repository;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.eventlogging.domain.CouchEventLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testApplicationEventLogging.xml")
public class AllCouchLogsIT {

    @Autowired
    private AllCouchLogs allCouchLogs;

    @Before
    public void setUp() {
        allCouchLogs.removeAll();
    }

    @Test
    public void shouldAddAndRetrieveLogsBySubject() {

        CouchEventLog couchLog = new CouchEventLog();
        couchLog.setSubject("org.motechproject.test");

        CouchEventLog couchLog2 = new CouchEventLog();
        couchLog2.setSubject("org.motechproject.test2");

        allCouchLogs.log(couchLog);
        allCouchLogs.log(couchLog2);

        List<CouchEventLog> logList = allCouchLogs
                .findAllBySubject("org.motechproject.test");
        assertEquals(logList.size(), 1);

        logList = allCouchLogs.findAllBySubject("org.motechproject.test2");

        assertEquals(logList.size(), 1);
    }

    @Test
    public void shouldAddAndRetrieveLogsByParameter() {

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("key1", "value1");
        parameters.put("key2", "value2");

        CouchEventLog couchLog = new CouchEventLog();
        couchLog.setSubject("org.motechproject.test");
        couchLog.setParameters(parameters);

        CouchEventLog couchLog2 = new CouchEventLog();
        couchLog2.setSubject("org.motechproject.test2");
        couchLog2.setParameters(parameters);

        allCouchLogs.log(couchLog);
        allCouchLogs.log(couchLog2);

        List<CouchEventLog> logList = allCouchLogs.findAllByParameter("key1",
                "value1");

        assertEquals(logList.size(), 2);

        logList = allCouchLogs.findAllByParameter("key2", "value2");

        assertEquals(logList.size(), 2);

        logList = allCouchLogs.findAllByParameter("key1", "value2");

        assertEquals(logList.size(), 0);

        logList = allCouchLogs.findAllByParameter("key3", "value3");

        assertEquals(logList.size(), 0);
    }

    @Test
    public void shouldAddAndRetrieveLogsBySubjectAndParameter() {

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("key1", "value1");
        parameters.put("key2", "value2");

        CouchEventLog couchLog = new CouchEventLog();
        couchLog.setSubject("org.motechproject.test");
        couchLog.setParameters(parameters);

        CouchEventLog couchLog2 = new CouchEventLog();
        couchLog2.setSubject("org.motechproject.test2");
        couchLog2.setParameters(parameters);

        allCouchLogs.log(couchLog);
        allCouchLogs.log(couchLog2);

        List<CouchEventLog> logList = allCouchLogs
                .findAllBySubjectAndParameter("org.motechproject.test", "key1",
                        "value1");
        assertEquals(logList.size(), 1);

        logList = allCouchLogs.findAllBySubjectAndParameter(
                "org.motechproject.test2", "key1", "value1");
        assertEquals(logList.size(), 1);
    }

    @After
    public void tearDown() {
        allCouchLogs.removeAll();
    }
}
