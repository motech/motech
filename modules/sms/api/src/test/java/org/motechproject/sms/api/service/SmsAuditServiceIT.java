package org.motechproject.sms.api.service;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.sms.api.SMSType;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.repository.AllSmsRecords;
import org.motechproject.sms.api.domain.SmsRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationSmsApi.xml"})
public class SmsAuditServiceIT {

    @Autowired
    private SmsAuditService smsAuditService;
    @Autowired
    private AllSmsRecords allSmsRecords;

    @Test
    public void shouldRetrieveSmsAuditRecord() {
        SmsRecord smsRecord = createSmsRecord(SMSType.OUTBOUND);
        smsAuditService.log(smsRecord);
        List<SmsRecord> smsRecords = smsAuditService.findAllSmsRecords();
        assertNotNull(smsRecords);
        assertTrue(smsRecords.size() > 0);
        assertEquals(smsRecords.get(0).getPhoneNumber(), smsRecord.getPhoneNumber());
    }

    @Test
    public void shouldRetrieveSmsRecordWithSearchCriteria() {
        smsAuditService.log(createSmsRecord(SMSType.INBOUND));
        smsAuditService.log(createSmsRecord(SMSType.OUTBOUND));

        Set<SMSType> smsTypes = new HashSet<>();
        smsTypes.add(SMSType.INBOUND);
        smsTypes.add(SMSType.OUTBOUND);
        SmsRecordSearchCriteria criteria = new SmsRecordSearchCriteria().withSmsTypes(smsTypes);
        SmsRecords smsRecords = smsAuditService.findAllSmsRecords(criteria);
        assertNotNull(smsRecords.getRecords());
        assertThat(smsRecords.getRecords().size(), is(2));
    }

    private SmsRecord createSmsRecord(SMSType smsType) {
        return new SmsRecord(smsType, "1234", "Hi", DateTime.now(), DeliveryStatus.INPROGRESS, "1");
    }

    @After
    public void tearDown() {
        allSmsRecords.removeAll();
    }

}
