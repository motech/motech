package org.motechproject.mds.test.service.relationshipswithhistory;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.test.domain.relationshipswithhistory.State;

public interface StateDataService extends MotechDataService<State> {

    @Lookup
    State findByName(@LookupField(name = "name") String name);
}
