package org.motechproject.email.service.impl;

import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.email.builder.EmailRecordSearchCriteria;
import org.motechproject.email.service.EmailRecordService;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The <code>EmailAuditServiceImpl</code> class provides API for everything connected with logging e-mails
 * and searching through them
 */
@Service("emailAuditService")
public class EmailAuditServiceImpl implements EmailAuditService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailAuditServiceImpl.class);

    private static final String EMAIL_LOG_BODY = "mail.log.body";
    private static final String EMAIL_LOG_ADDRESS = "mail.log.address";
    private static final String EMAIL_LOG_SUBJECT = "mail.log.subject";

    private static final String FALSE = "false";

    private EmailRecordService emailRecordService;
    private SettingsFacade settings;

    @Autowired
    public EmailAuditServiceImpl(EmailRecordService emailRecordService, @Qualifier("emailSettings") SettingsFacade settings) {
        this.emailRecordService = emailRecordService;
        this.settings = settings;
    }

    @Override
    public void log(EmailRecord emailRecord) {
        if (FALSE.equals(settings.getProperty(EMAIL_LOG_BODY))) {
            emailRecord.setMessage("");
        }

        if (FALSE.equals(settings.getProperty(EMAIL_LOG_ADDRESS))) {
            emailRecord.setFromAddress("");
            emailRecord.setToAddress("");
        }

        if (FALSE.equals(settings.getProperty(EMAIL_LOG_SUBJECT))) {
            emailRecord.setSubject("");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Logging: {}", emailRecord.toString());
        }

        emailRecordService.create(emailRecord);
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
        return emailRecordService.find(criteria.getFromAddress(), criteria.getToAddress(), criteria.getSubject(),
            criteria.getMessage(), criteria.getDeliveryTimeRange(), criteria.getDeliveryStatuses(),
            criteria.getQueryParams());
    }

    @Override
    public long countEmailRecords(EmailRecordSearchCriteria criteria) {
        return emailRecordService.countFind(criteria.getFromAddress(), criteria.getToAddress(), criteria.getSubject(),
                criteria.getMessage(), criteria.getDeliveryTimeRange(), criteria.getDeliveryStatuses());
    }
}
