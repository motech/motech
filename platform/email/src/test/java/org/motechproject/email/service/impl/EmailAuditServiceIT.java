package org.motechproject.email.service.impl;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.Range;
import org.motechproject.email.builder.EmailRecordSearchCriteria;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.email.service.EmailRecordService;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
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

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        EmailRecord emailRecord = createEmailRecord("to@address", "topic", DeliveryStatus.SENT);
        emailAuditService.log(emailRecord);
        List<EmailRecord> emailRecords = emailAuditService.findAllEmailRecords();
        assertNotNull(emailRecords);
        assertTrue(emailRecords.size() > 0);
        assertEquals(emailRecord.getFromAddress(), emailRecords.get(0).getFromAddress());
        assertEquals(emailRecord.getToAddress(), emailRecords.get(0).getToAddress());
        assertEquals("topic", emailRecords.get(0).getSubject());
    }

    @Test
    public void shouldRetrieveEmailRecordWithSearchCriteria() {
        emailAuditService.log(createEmailRecord("to1@address", "s 1", DeliveryStatus.SENT));
        emailAuditService.log(createEmailRecord("to@address2", "s 2", DeliveryStatus.SENT));

        Set<DeliveryStatus> deliveryStatuses = new HashSet<>();
        deliveryStatuses.add(DeliveryStatus.SENT);

        EmailRecordSearchCriteria criteriaToAddress = new EmailRecordSearchCriteria().withToAddress("to@address").
                withDeliveryStatuses(deliveryStatuses);
        List<EmailRecord> emailRecordsToAddress = emailAuditService.findEmailRecords(criteriaToAddress);
        assertNotNull(emailRecordsToAddress);
        assertEquals(1, emailRecordsToAddress.size());
    }

    @Test
    public void shouldCountAndRetrieveEmailsBasedOnCriteria() {
        emailAuditService.log(createEmailRecord("address@1.com", "something", DeliveryStatus.SENT));
        emailAuditService.log(createEmailRecord("address@2.com", "a subject", DeliveryStatus.SENT));
        emailAuditService.log(createEmailRecord("address3@1.com", "received this?", DeliveryStatus.RECEIVED));
        emailAuditService.log(createEmailRecord("something@1.com", "was sent", DeliveryStatus.SENT));
        emailAuditService.log(createEmailRecord("address@1.com", "error", DeliveryStatus.ERROR));

        QueryParams queryParams = new QueryParams(1, 10, new Order("subject", Order.Direction.DESC));
        EmailRecordSearchCriteria criteria = new EmailRecordSearchCriteria().withToAddress("address")
                .withDeliveryStatuses(DeliveryStatus.SENT, DeliveryStatus.RECEIVED)
                .withQueryParams(queryParams).withMessageTimeRange(
                        new Range<>(new DateTime(0), new DateTime(Long.MAX_VALUE)));

        assertEquals(3, emailAuditService.countEmailRecords(criteria));

        List<EmailRecord> records = emailAuditService.findEmailRecords(criteria);

        assertEquals(asList("something", "received this?", "a subject"),
                extract(records, on(EmailRecord.class).getSubject()));
        assertEquals(asList("address@1.com", "address3@1.com", "address@2.com"),
                extract(records, on(EmailRecord.class).getToAddress()));
        assertEquals(asList(DeliveryStatus.SENT, DeliveryStatus.RECEIVED, DeliveryStatus.SENT),
                extract(records, on(EmailRecord.class).getDeliveryStatus()));
    }

    private EmailRecord createEmailRecord(String toAddress, String subject, DeliveryStatus deliveryStatus) {
        return new EmailRecord("from@address", toAddress, subject, "message", DateTime.now(), deliveryStatus);
    }

    @After
    public void tearDown() {
        emailRecordService.deleteAll();
    }
}
