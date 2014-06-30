package org.motechproject.security.repository;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.security.domain.PasswordRecovery;

import java.util.List;

public interface PasswordRecoveriesDataService extends MotechDataService<PasswordRecovery> {

    @Lookup
    List<PasswordRecovery> findByExpirationDate(@LookupField(name = "expirationDate") Range<DateTime> range);

    @Lookup
    PasswordRecovery findForUser(@LookupField(name = "username") String username);

    @Lookup
    PasswordRecovery findForToken(@LookupField(name = "token") String token);

}
