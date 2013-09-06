package org.motechproject.email.service.impl;

import org.joda.time.DateTime;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The <code>PurgeEmailEventHandlerImpl</code> class is responsible for handling events,
 * connected with purging {@Link EmailRecord}s
 */

@Service
public class PurgeEmailEventHandlerImpl {

    public static final String PURGE_EMAIL_SUBJECT = "PurgeMailsJob";

    @Autowired
    private EmailAuditServiceImpl auditService;

    @MotechListener (subjects = { PURGE_EMAIL_SUBJECT } )
    public void handle(MotechEvent event) {
        String purgeTime = (String) event.getParameters().get("purgeTime");
        String purgeMultiplier = (String) event.getParameters().get("purgeMultiplier");

        DateTime deadline;

        switch (purgeMultiplier) {
            case "hours" :
                deadline = DateTime.now().toLocalDateTime().minusHours(Integer.parseInt(purgeTime)).toDateTime();
                /* Using LocalDateTime, we make sure that a correct hour will be used in case of
                   change in the TimeZone */
                break;
            case "days" :
                deadline = DateTime.now().minusDays(Integer.parseInt(purgeTime));
                break;
            case "weeks" :
                deadline = DateTime.now().minusWeeks(Integer.parseInt(purgeTime));
                break;
            case "months" :
                deadline = DateTime.now().minusMonths(Integer.parseInt(purgeTime));
                break;
            case "years" : // Fall through to the default value
            default :
                deadline = DateTime.now().minusYears(Integer.parseInt(purgeTime));
                break;
        }

        List<EmailRecord> emailRecordList = auditService.findAllEmailRecords();

        for (EmailRecord record : emailRecordList) {
            if (record.getDeliveryTime().isBefore(deadline)) {
                auditService.delete(record);
            }
        }
    }

}
