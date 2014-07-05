package org.motechproject.hub.mds.service;

import java.util.List;

import org.motechproject.hub.mds.HubSubscription;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

public interface HubSubscriptionMDSService extends
        MotechDataService<HubSubscription> {

    @Lookup(name = "By Topic")
    List<HubSubscription> findSubByTopicId(
            @LookupField(name = "hubTopicId") Integer hubTopicId);

    @Lookup(name = "By CallbackUrl")
    List<HubSubscription> findSubByCallbackUrl(
            @LookupField(name = "callbackUrl") String callbackUrl);

    @Lookup(name = "By CallbackUrl and Topic")
    List<HubSubscription> findSubByCallbackUrlAndTopicId(
            @LookupField(name = "callbackUrl") String callbackUrl,
            @LookupField(name = "hubTopicId") Integer hubTopicId);

}
