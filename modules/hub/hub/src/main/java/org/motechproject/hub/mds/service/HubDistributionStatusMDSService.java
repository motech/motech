package org.motechproject.hub.mds.service;

import java.util.List;

import org.motechproject.hub.mds.HubDistributionStatus;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

public interface HubDistributionStatusMDSService extends
        MotechDataService<HubDistributionStatus> {

    @Lookup(name = "By distributionStatusId")
    List<HubDistributionStatus> findByDistStatus(
            @LookupField(name = "distributionStatusId") Integer distributionStatusId);
}
