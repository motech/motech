package org.motechproject.mds.test.service;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.test.domain.Patient;

public interface PatientDataService extends MotechDataService<Patient> {

    @Lookup
    Patient findByName(@LookupField(name = "name") String name);
}
