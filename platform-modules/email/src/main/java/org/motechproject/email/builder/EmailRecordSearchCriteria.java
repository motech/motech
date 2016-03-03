package org.motechproject.email.builder;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.mds.query.QueryParams;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The <code>EmailRecordSearchCriteria</code> class represents search criteria that may be
 * used for searching {@link org.motechproject.email.domain.EmailRecord} entities in Motech
 * Data Services. A consumer of this class may create search criteria to query on multiple
 * fields by calling several of the with* methods. To perform the search, use
 * {@link org.motechproject.email.service.EmailAuditService#findEmailRecords(EmailRecordSearchCriteria)}.
 */

public class EmailRecordSearchCriteria {

    private String fromAddress;
    private String toAddress;
    private String subject;
    private String message;
    private Range<DateTime> deliveryTimeRange;
    private Set<DeliveryStatus> deliveryStatuses = new HashSet<>();
    private QueryParams queryParams;

    /**
     * Sets the fromAddress criterion to the address specified
     *
     * @param fromAddress  the sender email address on which to search
     * @return this <code>EmailRecordSearchCriteria</code> with its fromAddress criterion
     * set to the provided address
     */
    public EmailRecordSearchCriteria withFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
        return this;
    }

    /**
     * Sets the toAddress criterion to the address specified
     *
     * @param toAddress  the recipient email address on which to search
     * @return this <code>EmailRecordSearchCriteria</code> with its toAddress criterion
     * set to the provided address
     */
    public EmailRecordSearchCriteria withToAddress(String toAddress) {
        this.toAddress = toAddress;
        return this;
    }

    /**
     * Sets the subject criterion to the subject specified
     *
     * @param subject  the subject on which to search
     * @return this <code>EmailRecordSearchCriteria</code> with its subject criterion
     * set to the provided subject
     */
    public EmailRecordSearchCriteria withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    /**
     * Sets the message criterion to the message specified
     *
     * @param message  the email message body on which to search
     * @return this <code>EmailRecordSearchCriteria</code> with its message criterion
     * set to the provided message
     */
    public EmailRecordSearchCriteria withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Sets the send time criterion to the time specified. Use this method to search on a
     * specific date/time; if a range is needed, use <code>withMessageTimeRange</code> instead.
     *
     * @param deliveryTimeRange  the specific time on which to search
     * @return this <code>EmailRecordSearchCriteria</code> with its deliveryTimeRange criterion
     * set to the specified date/time
     */
    public EmailRecordSearchCriteria withMessageTime(DateTime deliveryTimeRange) {
        this.deliveryTimeRange = new Range<>(deliveryTimeRange, deliveryTimeRange);
        return this;
    }

    /**
     * Sets the sent time criterion to the range specified. Use this method to search on a
     * time range; if searching on a specific date/time is needed, use <code>withMessageTime</code>
     * instead.
     *
     * @param deliveryTimeRange  the date/time range on which to search
     * @return this <code>EmailRecordSearchCriteria</code> with its deliveryTimeRange criterion
     * set to the specified date/time range
     */
    public EmailRecordSearchCriteria withMessageTimeRange(Range<DateTime> deliveryTimeRange) {
        this.deliveryTimeRange = deliveryTimeRange;
        return this;
    }

    /**
     * Sets the delivery statuses criterion to the set specified
     *
     * @param deliveryStatuses  the delivery statuses on which to search
     * @return this <code>EmailRecordSearchCriteria</code> with its deliveryStatuses criterion
     * set to the provided statuses
     */
    public EmailRecordSearchCriteria withDeliveryStatuses(Set<DeliveryStatus> deliveryStatuses) {
        this.deliveryStatuses.addAll(deliveryStatuses);
        return this;
    }

    /**
     * Sets the delivery statuses criterion to the set specified
     *
     * @param deliveryStatuses  the delivery statuses on which to search
     * @return this <code>EmailRecordSearchCriteria</code> with its deliveryStatuses criterion
     * set to the provided statuses
     */
    public EmailRecordSearchCriteria withDeliveryStatuses(DeliveryStatus... deliveryStatuses) {
        this.deliveryStatuses.addAll(Arrays.asList(deliveryStatuses));
        return this;
    }

    /**
     * Sets the queryParams of the search criteria to the parameters specified. Use this method
     * when it is necessary to specify order and size of query results. This is used mainly for
     * paging/ordering queries from the UI.
     *
     * @param queryParams  the query parameters to include with the search criteria
     * @return this <code>EmailRecordSearchCriteria</code> with its queryParams set to the provided
     * parameters
     */
    public EmailRecordSearchCriteria withQueryParams(QueryParams queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    /**
     * Gets the from address criterion.
     *
     * @return the fromAddress criterion for this search criteria
     */
    public String getFromAddress() {
        return fromAddress;
    }

    /**
     * Gets the to address criterion.
     *
     * @return the toAddress criterion for this search criteria
     */
    public String getToAddress() {
        return toAddress;
    }

    /**
     * Gets the subject criterion.
     *
     * @return the subject criterion for this search criteria
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the message body criterion.
     *
     * @return the message criterion for this search criteria
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the delivery time range criterion.
     *
     * @return the deliveryTimeRange criterion for this search criteria
     */
    public Range<DateTime> getDeliveryTimeRange() {
        return deliveryTimeRange;
    }

    /**
     * Gets the delivery statuses criterion.
     *
     * @return the delivery statuses criterion for this search criteria
     */
    public Set<DeliveryStatus> getDeliveryStatuses() {
        return deliveryStatuses;
    }

    /**
     * Gets the query paramaters that are used for controlling order and size of the
     * query results for this search criteria.
     *
     * @return the query params for this search criteria
     */
    public QueryParams getQueryParams() {
        return queryParams;
    }
}
