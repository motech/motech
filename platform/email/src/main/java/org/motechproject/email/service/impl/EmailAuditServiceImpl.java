package org.motechproject.email.service.impl;

import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.search.CountSearch;
import org.motechproject.email.search.RecordSearch;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.email.builder.EmailRecordSearchCriteria;
import org.motechproject.email.service.EmailRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The <code>EmailAuditServiceImpl</code> class provides API for everything connected with logging e-mails
 * and searching through them
 */
@Service("emailAuditService")
public class EmailAuditServiceImpl implements EmailAuditService {

    private EmailRecordService emailRecordService;

    @Autowired
    public EmailAuditServiceImpl(EmailRecordService emailRecordService) {
        this.emailRecordService = emailRecordService;
    }

    @Override
    public EmailRecord findById(long id) {
        return emailRecordService.retrieve("id", id);
    }

    @Override
    public List<EmailRecord> findAllEmailRecords() {
        return emailRecordService.retrieveAll();
    }

    @Override
    public void delete(EmailRecord emailRecord) {
        emailRecordService.delete(emailRecord);
    }

    @Override
    public List<EmailRecord> findEmailRecords(EmailRecordSearchCriteria criteria) {
        return emailRecordService.executeQuery(new RecordSearch(criteria));
    }

    @Override
    public long countEmailRecords(EmailRecordSearchCriteria criteria) {
        return emailRecordService.executeQuery(new CountSearch(criteria));
    }
}
