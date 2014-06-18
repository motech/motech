package org.motechproject.security.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.security.domain.MotechURLSecurityRule;

import java.util.List;

public interface MotechURLSecurityRuleDataService extends MotechDataService<MotechURLSecurityRule> {

    @Lookup
    List<MotechURLSecurityRule> findByOrigin(@LookupField(name = "origin") String origin);

}
