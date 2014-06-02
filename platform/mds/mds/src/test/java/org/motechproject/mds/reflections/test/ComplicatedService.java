package org.motechproject.mds.reflections.test;


import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.builder.SampleWithLookups;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;

public interface ComplicatedService extends EmptyInterface, MotechDataService<SampleWithLookups> {
    @Lookup
    public SampleWithLookups findByNothing();
    @Lookup
    public List<SampleWithLookups> findCollectionByNothing();
}
