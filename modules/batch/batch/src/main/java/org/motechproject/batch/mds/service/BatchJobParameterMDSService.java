package org.motechproject.batch.mds.service;

import java.util.List;

import org.motechproject.batch.mds.BatchJobParameters;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

public interface BatchJobParameterMDSService extends
        MotechDataService<BatchJobParameters> {
    @Lookup(name = "By JobId")
    List<BatchJobParameters> findByJobId(
            @LookupField(name = "batchJobId") Integer batchJobId);

}
