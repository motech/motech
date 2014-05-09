package org.motechproject.email.service.impl;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.email.builder.EmailRecordSearchCriteria;
import org.motechproject.email.service.EmailRecordService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class EmailAuditServiceIT extends BasePaxIT {

    @Inject
    private EmailAuditService emailAuditService;

    @Inject
    private EmailRecordService emailRecordService;

    @Test
    public void shouldRetrieveEmailAuditRecord() {
        EmailRecord emailRecord = createEmailRecord("to@address", DeliveryStatus.SENT);
        emailAuditService.log(emailRecord);
        List<EmailRecord> emailRecords = emailAuditService.findAllEmailRecords();
        assertNotNull(emailRecords);
        assertTrue(emailRecords.size() > 0);
        assertEquals(emailRecord.getFromAddress(), emailRecords.get(0).getFromAddress());
        assertEquals(emailRecord.getToAddress(), emailRecords.get(0).getToAddress());
    }

    @Test
    public void shouldRetrieveEmailRecordWithSearchCriteria() {
        emailAuditService.log(createEmailRecord("to@address", DeliveryStatus.SENT));
        emailAuditService.log(createEmailRecord("to@address2", DeliveryStatus.SENT));

        Set<DeliveryStatus> deliveryStatuses = new HashSet<>();
        deliveryStatuses.add(DeliveryStatus.SENT);

        EmailRecordSearchCriteria criteriaToAddress = new EmailRecordSearchCriteria().withToAddress("to@address").
                withDeliveryStatuses(deliveryStatuses);
        List <EmailRecord> emailRecordsToAddress = emailAuditService.findEmailRecords(criteriaToAddress);
        assertNotNull(emailRecordsToAddress);
        assertEquals(1, emailRecordsToAddress.size());
    }

    private EmailRecord createEmailRecord(String toAddress, DeliveryStatus deliveryStatus) {
        return new EmailRecord("from@address", toAddress, "subject", "message", DateTime.now(), deliveryStatus);
    }

    @After
    public void tearDown() {
        emailRecordService.deleteAll();
    }
}
