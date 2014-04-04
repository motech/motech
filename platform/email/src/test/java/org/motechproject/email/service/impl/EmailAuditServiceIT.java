package org.motechproject.email.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.testing.utils.osgi.BasePaxIT;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class EmailAuditServiceIT extends BasePaxIT {

    @Inject
    private EmailSenderService emailSenderService;


    @Test
    public void test() {
        assertNotNull(emailSenderService);
    }

/*    @Autowired
    private EmailAuditService emailAuditService;

    @Autowired
    private EmailRecordService emailRecordService;*/

/*    @Test
    public void shouldRetrieveEmailAuditRecord() {
        EmailRecord emailRecord = createEmailRecord("to@address", DeliveryStatus.SENT);
        emailAuditService.log(emailRecord);
        List<EmailRecord> emailRecords = emailAuditService.findAllEmailRecords();
        assertNotNull(emailRecords);
        assertTrue(emailRecords.size() > 0);
        assertEquals(emailRecords.get(0).getFromAddress(), emailRecord.getFromAddress());
        assertEquals(emailRecords.get(0).getToAddress(), emailRecord.getToAddress());
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
        assertThat(emailRecordsToAddress.size(), is(1));
    }

    private EmailRecord createEmailRecord(String toAddress, DeliveryStatus deliveryStatus) {
        return new EmailRecord("from@address", toAddress, "subject", "message", DateUtil.now(), deliveryStatus);
    }

    @After
    public void tearDown() {
        emailRecordService.deleteAll();
    }*/

}
