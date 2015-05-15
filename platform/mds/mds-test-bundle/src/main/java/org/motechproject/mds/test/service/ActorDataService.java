package org.motechproject.mds.test.service;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.test.domain.Actor;

public interface ActorDataService extends MotechDataService<Actor> {
    @Lookup
    Actor findByName(@LookupField(name = "name") String name);
}
