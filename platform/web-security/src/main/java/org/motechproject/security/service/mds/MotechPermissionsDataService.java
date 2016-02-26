package org.motechproject.security.service.mds;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.security.domain.MotechPermission;

/**
 * Interface for data service injected by MDS
 */
public interface MotechPermissionsDataService extends MotechDataService<MotechPermission> {

    @Lookup
    MotechPermission findByPermissionName(@LookupField(name = "permissionName") String permissionName);

}
