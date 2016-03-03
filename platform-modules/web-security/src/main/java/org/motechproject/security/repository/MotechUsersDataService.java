package org.motechproject.security.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.security.domain.MotechUser;

import java.util.List;

/**
 * Interface for data service injected by MDS
 */
public interface MotechUsersDataService extends MotechDataService<MotechUser> {

    @Lookup
    MotechUser findByUserName(@LookupField(name = "userName", customOperator = "equalsIgnoreCase()") String userName);

    @Lookup
    MotechUser findByOpenId(@LookupField(name = "openId") String openId);

    @Lookup
    MotechUser findByEmail(@LookupField(name = "email") String email);

    @Lookup
    List<MotechUser> findByRole(@LookupField(name = "roles") String role);

    @Lookup
    List<MotechUser> findByExternalId(@LookupField(name = "externalId") String externalId);

}
