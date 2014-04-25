package org.motechproject.admin.service;

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

    @Lookup
    List<StatusMessage> findByTimeout(@LookupField(name = "timeout") Range<DateTime> timeout);

}
