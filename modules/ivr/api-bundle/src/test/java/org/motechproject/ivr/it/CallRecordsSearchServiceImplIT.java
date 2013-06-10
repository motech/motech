package org.motechproject.ivr.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.domain.CallDirection;
import org.motechproject.ivr.domain.CallDisposition;
import org.motechproject.ivr.domain.CallRecordSearchParameters;
import org.motechproject.ivr.repository.AllCallDetailRecords;
import org.motechproject.ivr.service.contract.CallRecordsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class CallRecordsSearchServiceImplIT {
    private static final String PHONE_NUMBER = "232";
    @Autowired
    CallRecordsSearchService calllogSearchService;

    @Autowired
    AllCallDetailRecords repository;

    @Before
    public void setUp() throws Exception {
        final CallDetailRecord log = new CallDetailRecord("a", PHONE_NUMBER);
        log.setAnswerDate(DateUtil.now().toDate());
        log.setStartDate(DateUtil.now());
        log.setEndDate(DateUtil.now());
        log.setDuration(34);
        log.setCallDirection(CallDirection.Inbound);
        log.setDisposition(CallDisposition.UNKNOWN);
        repository.add(log);
        final CallDetailRecord b = repository.findOrCreate("b", PHONE_NUMBER + "23");
        b.setDisposition(CallDisposition.ANSWERED);
        b.setCallDirection(CallDirection.Outbound);
        b.setDuration(324);
        repository.update(b);
    }

    @Test
    public void shouldSearchCalllogByPhoneNumber() throws Exception {
        final CallRecordSearchParameters searchParameters = new CallRecordSearchParameters();
        searchParameters.setPhoneNumber(PHONE_NUMBER);
        final List<CallDetailRecord> calllogs = calllogSearchService.search(searchParameters);
        assertEquals(PHONE_NUMBER, calllogs.get(0).getPhoneNumber());
    }

    @Test
    public void shouldReturnAllCalllogs() throws Exception {
        final CallRecordSearchParameters searchParameters = new CallRecordSearchParameters();
        final List<CallDetailRecord> calllogs = calllogSearchService.search(searchParameters);
        assertTrue(calllogs.size() >= 2);
    }

    @After
    public void tearDown() {
        repository.remove(repository.findByCallId("a"));
        repository.remove(repository.findByCallId("b"));
    }
}
