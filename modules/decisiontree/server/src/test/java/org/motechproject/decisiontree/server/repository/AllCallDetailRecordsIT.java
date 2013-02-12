package org.motechproject.decisiontree.server.repository;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.decisiontree.core.CallDetail;
import org.motechproject.decisiontree.server.domain.CallDetailRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllCallDetailRecordsIT {

    public static final String PHONE_NUMBER_1 = "99991234561";
    public static final String PHONE_NUMBER_2 = "99991234671";
    @Autowired AllCallDetailRecords allCallDetailRecords;
    private static final int PAGE_SIZE = 10;

    @Before
    public void setUp() {
        allCallDetailRecords.add(getRecord(PHONE_NUMBER_1));
        allCallDetailRecords.add(getRecord(PHONE_NUMBER_2));
    }

    private CallDetailRecord getRecord(String phoneNumber) {
        final CallDetailRecord log = new CallDetailRecord("1", phoneNumber);
        log.setAnswerDate(DateUtil.now().toDate());
        log.setStartDate(DateUtil.now());
        log.setEndDate(DateUtil.now());
        log.setDuration(34);
        log.setDisposition(CallDetailRecord.Disposition.UNKNOWN);
        return log;
    }

    @Test
    public void shouldSearchCalllogs() throws Exception {
        DateTime endTime = DateTime.now().plusDays(1);
        DateTime startTime = DateTime.now().minusDays(1);
        int maxDuration = 34;
        final List<CallDetail> rowList = allCallDetailRecords.search(PHONE_NUMBER_1, startTime, endTime,  0, maxDuration, Arrays.asList(CallDetailRecord.Disposition.UNKNOWN.name()), 0, PAGE_SIZE, null, false);
        assertTrue(rowList.size()>0);
    }

    @Test
    public void shouldSearchCallsWithSpecificDuration() throws Exception {
        final List<CallDetail> rowList = allCallDetailRecords.search(null, null, null, null, null, null, 0, PAGE_SIZE, null, false);
        assertTrue(rowList.size()>0);
    }

    @Test
    public void shouldReturnBasedOnGivenSortByParamInDescendingOrder() throws Exception{
        List<CallDetail> rowList = allCallDetailRecords.search("99991234*", null, null, null, null, null, 0, PAGE_SIZE, "phoneNumber", true);
        assertEquals(rowList.get(0).getPhoneNumber(), PHONE_NUMBER_2);

    }

    @Test
    public void shouldReturnBasedOnGivenSortByParamInAscendingOrder() throws Exception{
        List<CallDetail> rowList = allCallDetailRecords.search("99991234*", null, null, null, null, null, 0, PAGE_SIZE, "phoneNumber", false);
        assertEquals(PHONE_NUMBER_1, rowList.get(0).getPhoneNumber());
    }

    @After
    public void tearDown() {
        final List<CallDetail> logs = allCallDetailRecords.search(PHONE_NUMBER_1, null, null, null, null, null, 0, PAGE_SIZE, null, false);
        logs.addAll(allCallDetailRecords.search(PHONE_NUMBER_2, null, null, null, null, null, 0, PAGE_SIZE, null, false));
        for (CallDetail log:logs) {
           allCallDetailRecords.remove((CallDetailRecord) log);
        }
    }

}
