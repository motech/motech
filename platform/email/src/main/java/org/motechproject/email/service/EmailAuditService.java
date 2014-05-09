package org.motechproject.email.service;

import org.motechproject.email.builder.EmailRecordSearchCriteria;
import org.motechproject.email.domain.EmailRecord;

import java.util.List;

/**
 * Specifies methods that are implemented within the service
 */

public interface EmailAuditService {

    void log(EmailRecord mailRecord);

    EmailRecord findById(long id);

    List<EmailRecord> findAllEmailRecords();

    List<EmailRecord> findEmailRecords(EmailRecordSearchCriteria criteria);

    long countEmailRecords(EmailRecordSearchCriteria criteria);

    void delete(EmailRecord emailRecord);
}
