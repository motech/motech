package org.motechproject.hub.mds.service;

import java.util.List;

import org.motechproject.hub.mds.HubSubscriberTransaction;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

public interface HubSubscriberTransactionMDSService extends
        MotechDataService<HubSubscriberTransaction> {
    @Lookup(name = "By hubSubscriptionId")
    List<HubSubscriberTransaction> findSubTransBySubId(
            @LookupField(name = "hubSubscriptionId") Integer hubSubscriptionId);

}
