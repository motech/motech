package org.motechproject.security.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.security.domain.MotechRole;

public interface MotechRolesDataService extends MotechDataService<MotechRole> {

    @Lookup
    MotechRole findByRoleName(@LookupField(name = "roleName") String roleName);

}
