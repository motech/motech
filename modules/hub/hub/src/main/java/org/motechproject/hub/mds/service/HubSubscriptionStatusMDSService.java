package org.motechproject.hub.mds.service;

import java.util.List;

import org.motechproject.hub.mds.HubSubscriptionStatus;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

public interface HubSubscriptionStatusMDSService extends
        MotechDataService<HubSubscriptionStatus> {
    @Lookup(name = "By subscriptionStatusId")
    List<HubSubscriptionStatus> findBySubStatus(
            @LookupField(name = "subscriptionStatusId") Integer subscriptionStatusId);

}
