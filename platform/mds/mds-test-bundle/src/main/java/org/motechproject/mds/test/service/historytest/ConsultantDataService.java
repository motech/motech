package org.motechproject.mds.test.service.historytest;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.test.domain.historytest.Consultant;

public interface ConsultantDataService extends MotechDataService<Consultant> {

    @Lookup
    Consultant findByName(@LookupField(name = "name") String name);
}
