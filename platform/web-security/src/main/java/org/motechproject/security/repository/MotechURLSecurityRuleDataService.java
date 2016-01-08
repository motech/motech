package org.motechproject.security.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.security.domain.MotechURLSecurityRule;

import java.util.List;

/**
 * Interface for data service injected by MDS
 */
public interface MotechURLSecurityRuleDataService extends MotechDataService<MotechURLSecurityRule> {

    @Lookup
    List<MotechURLSecurityRule> findByOrigin(@LookupField(name = "origin") String origin);

    @Lookup
    List<MotechURLSecurityRule> findByOriginAndVersion(@LookupField(name = "origin") String origin, @LookupField(name = "version") String version);

}
