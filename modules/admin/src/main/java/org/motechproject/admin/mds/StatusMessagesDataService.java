package org.motechproject.admin.mds;

import org.joda.time.DateTime;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;

/**
 * MDS data service for {@link StatusMessage}s.
 */
public interface StatusMessagesDataService extends MotechDataService<StatusMessage> {

    /**
     * Returns status messages with their timeout value in a given range. Leaving the min value empty will
     * result in retrieving all messages timing out before the max value. Leaving the max value empty will result
     * in retrieving all messages timing out after the min value.
     * @param timeout the range in which the timeout date-time must fall
     * @return a list of messages matching the timeout criteria
     */
    @Lookup
    List<StatusMessage> findByTimeout(@LookupField(name = "timeout") Range<DateTime> timeout);
}
