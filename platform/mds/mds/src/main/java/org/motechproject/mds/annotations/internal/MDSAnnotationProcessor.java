package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.reflections.MDSInterfaceResolver;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The <code>MDSAnnotationProcessor</code> class is responsible for scanning bundle contexts and
 * looking for classes, fields and methods containing MDS annotations, as well as processing them.
 *
 * @see org.motechproject.mds.annotations.internal.LookupProcessor
 * @see org.motechproject.mds.annotations.internal.EntityProcessor
 * @see org.motechproject.mds.annotations.internal.InstanceLifecycleListenerProcessor
 */
@Component
public class MDSAnnotationProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MDSAnnotationProcessor.class);

    private EntityProcessor entityProcessor;
    private LookupProcessor lookupProcessor;
    private InstanceLifecycleListenerProcessor instanceLifecycleListenerProcessor;

    public boolean processAnnotations(Bundle bundle) {
        String symbolicName = bundle.getSymbolicName();

        LOGGER.debug("Starting scanning bundle {} for MDS annotations.", symbolicName);

        entityProcessor.execute(bundle);
        lookupProcessor.execute(bundle);
        instanceLifecycleListenerProcessor.processAnnotations(bundle);

        LOGGER.debug("Finished scanning bundle {} for MDS annotations.", symbolicName);

        boolean mdsAnnotationsPresent = entityProcessor.hasFound() || lookupProcessor.hasFound();

        // If there's any MDS annotation present, we start scanning for MDS service interfaces in the bundle
        if (mdsAnnotationsPresent) {
            MDSInterfaceResolver.processMDSInterfaces(bundle);
        }

        return mdsAnnotationsPresent;
    }


    @Autowired
    public void setLookupProcessor(LookupProcessor lookupProcessor) {
        this.lookupProcessor = lookupProcessor;
    }

    @Autowired
    public void setEntityProcessor(EntityProcessor entityProcessor) {
        this.entityProcessor = entityProcessor;
    }

    @Autowired
    public void setInstanceLifecycleListenerProcessor(InstanceLifecycleListenerProcessor instanceLifecycleListenerProcessor) {
        this.instanceLifecycleListenerProcessor = instanceLifecycleListenerProcessor;
    }
}
