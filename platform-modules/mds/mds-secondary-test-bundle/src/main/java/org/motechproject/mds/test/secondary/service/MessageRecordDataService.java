package org.motechproject.mds.test.secondary.service;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.test.secondary.domain.MessageRecord;

import java.util.List;

public interface MessageRecordDataService extends MotechDataService<MessageRecord> {

    @Lookup
    List<MessageRecord> findByAuthor(@LookupField(name = "author") String author);
}
