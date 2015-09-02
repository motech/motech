package org.motechproject.email.service.impl;

import org.motechproject.email.domain.EmailRecord;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The <code>PurgeEmailEventHandlerImpl</code> class is responsible for handling events,
 * connected with purging {@link EmailRecord}s
 */

@Service
public class PurgeEmailEventHandlerImpl {

    public static final String PURGE_EMAIL_SUBJECT = "PurgeMailsJob";

    @Autowired
    private EmailAuditServiceImpl auditService;

    @MotechListener (subjects = { PURGE_EMAIL_SUBJECT })
    public void handle(MotechEvent event) {
        String purgeTime = (String) event.getParameters().get("purgeTime");
        String purgeMultiplier = (String) event.getParameters().get("purgeMultiplier");

        LocalDateTime deadline;

        switch (purgeMultiplier) {
            case "hours" :
                deadline = LocalDateTime.now().minusHours(Integer.parseInt(purgeTime));
                break;
            case "days" :
                deadline = LocalDateTime.now().minusDays(Integer.parseInt(purgeTime));
                break;
            case "weeks" :
                deadline = LocalDateTime.now().minusWeeks(Integer.parseInt(purgeTime));
                break;
            case "months" :
                deadline = LocalDateTime.now().minusMonths(Integer.parseInt(purgeTime));
                break;
            case "years" : // Fall through to the default value
            default :
                deadline = LocalDateTime.now().minusYears(Integer.parseInt(purgeTime));
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
