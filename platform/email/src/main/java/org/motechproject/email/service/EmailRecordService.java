package org.motechproject.email.service;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.QueryParams;

import java.util.List;
import java.util.Set;

/**
 * A service for this stub is created by Motech Data Services.
 */
public interface EmailRecordService extends MotechDataService<EmailRecord> {

    @Lookup(name = "Find")
    List<EmailRecord> find(
             @LookupField(name = "fromAddress") String fromAddress,
             @LookupField(name = "toAddress") String toAddress,
             @LookupField(name = "subject") String subject,
             @LookupField(name = "message") String message,
             @LookupField(name = "deliveryTime") Range<DateTime> deliveryTimeRange,
             @LookupField(name = "deliveryStatus") Set<DeliveryStatus> deliveryStatuses,
             QueryParams queryParams
    );

    List<EmailRecord> findByRecipientAddress(
            @LookupField String toAddress,
            QueryParams queryParams
    );
}
