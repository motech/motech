package org.motechproject.email.service;

import org.motechproject.email.domain.EmailRecord;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;

public interface EmailRecordService extends MotechDataService<EmailRecord> {

    @Lookup
    List<EmailRecord> byToAddress(@LookupField(name = "toAddress") String address);
}
