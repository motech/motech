package org.motechproject.hub.mds.service;

import java.util.List;

import org.motechproject.hub.mds.HubDistributionError;
import org.motechproject.hub.mds.HubSubscription;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

public interface HubDistributionErrorMDSService extends MotechDataService<HubDistributionError> { 
	 @Lookup(name = "By hubSubscriptionId")
	 List<HubDistributionError> findDistErrorBySubscription(@LookupField(name = "hubSubscriptionId") Integer hubSubscriptionId);

}
