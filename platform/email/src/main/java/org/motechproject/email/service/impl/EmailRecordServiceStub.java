package org.motechproject.email.service.impl;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.service.EmailRecordService;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.filter.Filter;
import org.motechproject.mds.util.QueryParams;

import java.util.List;
import java.util.Set;

//@Service
public class EmailRecordServiceStub implements EmailRecordService {
    @Override
    public List<EmailRecord> find(@LookupField(name = "fromAddress") String fromAddress, @LookupField(name = "toAddress") String toAddress, @LookupField(name = "subject") String subject, @LookupField(name = "message") String message, @LookupField(name = "deliveryTime") Range<DateTime> deliveryTimeRange, @LookupField(name = "deliveryStatus") Set<DeliveryStatus> deliveryStatuses, QueryParams queryParams) {
        return null;
    }

    @Override
    public List<EmailRecord> findByRecipientAddress(@LookupField String toAddress, QueryParams queryParams) {
        return null;
    }

    @Override
    public EmailRecord create(EmailRecord object) {
        return null;
    }

    @Override
    public EmailRecord retrieve(String primaryKeyName, Object value) {
        return null;
    }

    @Override
    public List<EmailRecord> retrieveAll() {
        return null;
    }

    @Override
    public List<EmailRecord> retrieveAll(QueryParams queryParams) {
        return null;
    }

    @Override
    public EmailRecord update(EmailRecord object) {
        return null;
    }

    @Override
    public void delete(EmailRecord object) {

    }

    @Override
    public void delete(String primaryKeyName, Object value) {

    }

    @Override
    public EmailRecord findTrashInstanceById(Object instanceId, Object entityId) {
        return null;
    }

    @Override
    public void revertFromTrash(Object newInstance, Object trash) {

    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public List<EmailRecord> filter(Filter filter) {
        return null;
    }

    @Override
    public List<EmailRecord> filter(Filter filter, QueryParams queryParams) {
        return null;
    }

    @Override
    public long countForFilter(Filter filter) {
        return 0;
    }

    @Override
    public void deleteAll() {

    }
}
