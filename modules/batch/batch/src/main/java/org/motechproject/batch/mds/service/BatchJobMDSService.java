package org.motechproject.batch.mds.service;

import java.util.List;

import org.motechproject.batch.mds.BatchJob;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

/**
 * Class to query BatchJob entity
 * 
 * @author beehyv
 * 
 */
public interface BatchJobMDSService extends MotechDataService<BatchJob> {

    @Lookup(name = "By JobName")
    List<BatchJob> findByJobName(@LookupField(name = "jobName") String jobName);

}
