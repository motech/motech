package org.motechproject.mds.annotations.internal;

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
 */
@Component
public class MDSAnnotationProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MDSAnnotationProcessor.class);

    private EntityProcessor entityProcessor;
    private LookupProcessor lookupProcessor;

    public boolean processAnnotations(Bundle bundle) {
        String symbolicName = bundle.getSymbolicName();

        LOGGER.debug("Starting scanning bundle {} for MDS annotations.", symbolicName);

        boolean annotationsFound = entityProcessor.execute(bundle);
        annotationsFound |= lookupProcessor.execute(bundle);

        LOGGER.debug("Finished scanning bundle {} for MDS annotations.", symbolicName);

        return annotationsFound;
    }



    @Autowired
    public void setLookupProcessor(LookupProcessor lookupProcessor) {
        this.lookupProcessor = lookupProcessor;
    }

    @Autowired
    public void setEntityProcessor(EntityProcessor entityProcessor) {
        this.entityProcessor = entityProcessor;
    }
}
