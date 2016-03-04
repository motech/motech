package org.motechproject.email.service;

import org.motechproject.email.builder.EmailRecordSearchCriteria;
import org.motechproject.email.domain.EmailRecord;

import java.util.List;

/**
 * The <code>EmailAuditService</code> interface provides methods for logging
 * email activity, as well as searching and deleting the email logs.
 */

public interface EmailAuditService {

    /**
     * Finds an <code>EmailRecord</code> in the log by ID.
     *
     * @param id  the identifier of the record to find
     * @return the email record that matches the provided identifier, or null if
     * no matching record exists
     */
    EmailRecord findById(long id);

    /**
     * Finds and returns all <code>EmailRecord</code> entries in the email log.
     *
     * @return all email records in the email log
     */
    List<EmailRecord> findAllEmailRecords();

    /**
     * Finds and returns all <code>EmailRecord</code> entries matching the specified
     * search criteria.
     *
     * @return all email records matching the provided criteria
     */
    List<EmailRecord> findEmailRecords(EmailRecordSearchCriteria criteria);

    /**
     * Returns the count of <code>EmailRecord</code> entries matching the specified
     * search criteria.
     *
     * @return the count of email records matching the provided criteria
     **/
    long countEmailRecords(EmailRecordSearchCriteria criteria);

    /**
     * Deletes the specified <code>EmailRecord</code> entry from the email log.
     */
    void delete(EmailRecord emailRecord);
}
