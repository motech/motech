package org.motechproject.security.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.security.domain.MotechPermission;

public interface MotechPermissionsDataService extends MotechDataService<MotechPermission> {

    @Lookup
    MotechPermission findByPermissionName(@LookupField(name = "permissionName") String permissionName);

}
