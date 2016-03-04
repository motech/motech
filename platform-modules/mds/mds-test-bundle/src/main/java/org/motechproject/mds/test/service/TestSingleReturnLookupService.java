package org.motechproject.mds.test.service;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.test.domain.TestSingleReturnLookup;

public interface TestSingleReturnLookupService extends MotechDataService<TestSingleReturnLookup> {

    @Lookup(name = "By First Field Name")
    TestSingleReturnLookup findByFirstFieldName(@LookupField(name="firstFieldName") String firstFieldName);

    @Lookup(name = "By Second Field Name")
    TestSingleReturnLookup findBySecondFieldName(@LookupField(name="secondFieldName") String secondFieldName);
}
