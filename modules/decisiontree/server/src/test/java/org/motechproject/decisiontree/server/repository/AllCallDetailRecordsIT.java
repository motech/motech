package org.motechproject.decisiontree.server.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.decisiontree.core.CallDetail;
import org.motechproject.decisiontree.server.domain.CallDetailRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllCallDetailRecordsIT {

    @Autowired AllCallDetailRecords allCallDetailRecords;

    @Before
    public void setUp() {
        final CallDetailRecord log = new CallDetailRecord("1", "123");
        log.setAnswerDate(DateUtil.now().toDate());
        log.setStartDate(DateUtil.now());
        log.setEndDate(DateUtil.now());
        log.setDuration(34);
        allCallDetailRecords.add(log);
    }

    @Test
    public void shouldSearchCalllogs() throws Exception {
        DateTime endTime = DateTime.now().plusDays(1);
        DateTime startTime = DateTime.now().minusDays(1);
        int maxDuration = 34;
        final List<CallDetail> rowList = allCallDetailRecords.search("123", startTime, endTime,  0, maxDuration);
        assertTrue(rowList.size()>0);
    }

    @Test
    public void shouldSearchCallsWithSpecificDuration() throws Exception {
        final List<CallDetail> rowList = allCallDetailRecords.search(null, null, null, null, null);
        assertTrue(rowList.size()>0);
    }

}
