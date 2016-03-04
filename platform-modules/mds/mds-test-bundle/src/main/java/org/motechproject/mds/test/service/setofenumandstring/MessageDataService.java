package org.motechproject.mds.test.service.setofenumandstring;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.test.domain.setofenumandstring.Message;

public interface MessageDataService extends MotechDataService<Message> {
    @Lookup
    Message getMessageBySubject(@LookupField(name = "subject") String subject);
}
