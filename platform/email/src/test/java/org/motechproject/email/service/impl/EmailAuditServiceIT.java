package org.motechproject.email.service.impl;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.email.service.EmailRecordSearchCriteria;
import org.motechproject.testing.osgi.BasePaxIT;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class EmailAuditServiceIT extends BasePaxIT {

    @Inject
    @Filter(timeout = 60000)
    private EmailAuditService emailAuditService;

    @Override
    protected String getDefaultLogLevel() {
        return "INFO";
    }

    @Override
    protected boolean startHttpServer() {
        return true;
    }

    @Test
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
        return new EmailRecord("from@address", toAddress, "subject", "message", DateTime.now(), deliveryStatus.name());
    }

    @After
    public void tearDown() {
        //allEmailRecords.removeAll();
    }

}
