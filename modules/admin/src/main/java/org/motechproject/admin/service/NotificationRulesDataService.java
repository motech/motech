package org.motechproject.admin.service;

import org.motechproject.admin.domain.NotificationRule;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

/**
 * MDS data service for {@link NotificationRule}s.
 */
public interface NotificationRulesDataService extends MotechDataService<NotificationRule> {

    @Lookup
    NotificationRule findById(@LookupField(name = "id") Long id);

}
