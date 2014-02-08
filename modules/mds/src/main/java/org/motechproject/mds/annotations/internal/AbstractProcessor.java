package org.motechproject.mds.annotations.internal;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

/**
 * The <code>AbstractProcessor</code> is a base class for every processor that would like to perform
 * some actions on objects with the given annotation. The name of a processor should start with
 * the name of an annotation. For example if there is an annotation 'A', the processor name should
 * be equal to 'AProcessor' and the {@link #getAnnotation()} method should return class definition
 * of the 'A' annotation.
 */
abstract class AbstractProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProcessor.class);

    private Bundle bundle;

    protected abstract Class<? extends Annotation> getAnnotation();

    protected abstract List<? extends AnnotatedElement> getElements();

    protected abstract void process(AnnotatedElement element);

    public void execute() {
        execute(null);
    }

    public void execute(Bundle bundle) {
        this.bundle = bundle;
        Class<? extends Annotation> annotation = getAnnotation();

        for (AnnotatedElement element : getElements()) {
            LOGGER.debug("Processing: Annotation: {} Object: {}", annotation.getName(), element);

            process(element);

            LOGGER.debug("Processed: Annotation: {} Object: {}", annotation.getName(), element);
        }
    }

    protected Bundle getBundle() {
        return bundle;
    }

    void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

}
