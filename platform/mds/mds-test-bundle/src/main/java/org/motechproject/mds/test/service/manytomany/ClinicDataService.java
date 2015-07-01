package org.motechproject.mds.test.service.manytomany;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.test.domain.manytomany.Clinic;

public interface ClinicDataService extends MotechDataService<Clinic> {

    @Lookup
    Clinic findByName(@LookupField(name = "name") String name);
}
