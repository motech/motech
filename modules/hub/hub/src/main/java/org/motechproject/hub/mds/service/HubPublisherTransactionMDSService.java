package org.motechproject.hub.mds.service;

import java.util.List;

import org.motechproject.hub.mds.HubPublisherTransaction;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

public interface HubPublisherTransactionMDSService extends
        MotechDataService<HubPublisherTransaction> {

    @Lookup(name = "By hubTopicId")
    List<HubPublisherTransaction> findPubTransactionByTopicId(
            @LookupField(name = "hubTopicId") Integer hubTopicId);

}
