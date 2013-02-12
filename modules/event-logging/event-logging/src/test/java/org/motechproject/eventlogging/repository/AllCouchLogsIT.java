package org.motechproject.eventlogging.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.eventlogging.domain.CouchEventLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals(asList("org.motechproject.test"), extract(logList, on(CouchEventLog.class).getSubject()));

        logList = allCouchLogs.findAllBySubject("org.motechproject.test2");

        assertEquals(asList("org.motechproject.test2"), extract(logList, on(CouchEventLog.class).getSubject()));;
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

        assertEquals(asList("org.motechproject.test", "org.motechproject.test2"), extract(logList, on(CouchEventLog.class).getSubject()));

        logList = allCouchLogs.findAllByParameter("key2", "value2");

        assertEquals(asList("org.motechproject.test", "org.motechproject.test2"), extract(logList, on(CouchEventLog.class).getSubject()));

        logList = allCouchLogs.findAllByParameter("key1", "value2");

        assertTrue(logList.isEmpty());

        logList = allCouchLogs.findAllByParameter("key3", "value3");

        assertTrue(logList.isEmpty());
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
        assertEquals(asList("org.motechproject.test"), extract(logList, on(CouchEventLog.class).getSubject()));

        logList = allCouchLogs.findAllBySubjectAndParameter(
                "org.motechproject.test2", "key1", "value1");
        assertEquals(asList("org.motechproject.test2"), extract(logList, on(CouchEventLog.class).getSubject()));
    }

    @After
    public void tearDown() {
        allCouchLogs.removeAll();
    }
}
