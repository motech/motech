package org.motechproject.server.logging.service;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.logging.dao.AllEventLogs;
import org.motechproject.server.logging.domain.EventLog;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContextEventLogging.xml"})
public class EventLoggingServiceIT {
    @Autowired
    private EventLoggingServiceImpl service;
    @Autowired
    private AllEventLogs allEventLogs;

    private EventLog log;

    @After
    public void tearDown() {
        if (log != null) allEventLogs.remove(log);
    }

    @Test
    public void shouldCreateANewEventLog() {
        DateTime now = DateUtil.now();
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("StartDate", now.toString());
        data.put("EndDate", now.toString());

        service.create("1", "PillReminderCall", "NewCallEvent", "", now, data);

        List<EventLog> allLogs = allEventLogs.getAll();
        assertEquals(1, allLogs.size());

        log = allLogs.get(0);
        assertEquals("1", log.getExternalId());
        assertEquals("PillReminderCall", log.getLogType());
        assertEquals("NewCallEvent", log.getName());
        assertEquals("", log.getDescription());
//        TODO: To be uncommented when joda datetime deserialization issue is fixed
//        assertEquals(now, log.getDateTime());
        assertNotNull(log.getData());
        assertEquals(2, log.getData().size());
        assertEquals(now.toString(), log.getData().get("StartDate"));
        assertEquals(now.toString(), log.getData().get("EndDate"));
    }
}